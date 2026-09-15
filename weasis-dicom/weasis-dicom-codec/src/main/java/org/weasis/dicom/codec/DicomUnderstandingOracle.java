/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.dicom.codec.utils.DicomMediaUtils;

/**
 * Headless DICOM-understanding oracle. Reads a Part-10 path (Composer / Dicom Light TS output) and
 * prints one JSON object on stdout. No PHI is emitted. Pixel gate: {@link DicomUnderstandingLimits}
 * (uncompressed EVR LE MONOCHROME2 W/L). See {@code
 * docs/architecture/clean-room-and-understanding.md}.
 *
 * <p>{@code disposition} is the oracle outcome, not a viewer success flag: {@value #ACCEPTED} only
 * when {@code understood} is true. Opened-but-unsupported raster objects use {@value
 * #NOT_UNDERSTOOD_DISPOSITION}.
 */
public final class DicomUnderstandingOracle {

  /** Human-readable reason prefix; detail appended after {@code : }. */
  public static final String NOT_UNDERSTOOD = "not understood";

  public static final String ACCEPTED = "accepted";
  public static final String SKIPPED = "skipped";
  public static final String NOT_UNDERSTOOD_DISPOSITION = "not_understood";

  private DicomUnderstandingOracle() {}

  public record Samples(int x0y0, int center) {}

  /**
   * Structured verdict. {@code reason} is {@value #NOT_UNDERSTOOD} unless {@code understood} is
   * true. No patient identifiers.
   */
  public record Verdict(
      String path,
      boolean opened,
      String transferSyntaxUid,
      String transferSyntax,
      String sopClassUid,
      String mime,
      String photometric,
      String disposition,
      boolean understood,
      Integer rows,
      Integer columns,
      Double window,
      Double level,
      Samples samples,
      String reason) {

    public String toJson() {
      StringBuilder sb = new StringBuilder(384);
      sb.append('{');
      jsonField(sb, true, "path", path);
      jsonBool(sb, false, "opened", opened);
      jsonField(sb, false, "transferSyntaxUid", transferSyntaxUid);
      jsonField(sb, false, "transferSyntax", transferSyntax);
      jsonField(sb, false, "sopClassUid", sopClassUid);
      jsonField(sb, false, "mime", mime);
      jsonField(sb, false, "photometric", photometric);
      jsonField(sb, false, "disposition", disposition);
      jsonBool(sb, false, "understood", understood);
      jsonNumber(sb, false, "rows", rows);
      jsonNumber(sb, false, "columns", columns);
      jsonNumber(sb, false, "window", window);
      jsonNumber(sb, false, "level", level);
      sb.append(",\"samples\":");
      if (samples == null) {
        sb.append("null");
      } else {
        sb.append("{\"x0y0\":")
            .append(samples.x0y0())
            .append(",\"center\":")
            .append(samples.center())
            .append('}');
      }
      jsonField(sb, false, "reason", reason);
      sb.append('}');
      return sb.toString();
    }
  }

  public static Verdict evaluate(Path path) {
    String shown = path == null ? "" : path.toString();
    if (path == null || !Files.isRegularFile(path)) {
      return closed(shown, reason(NOT_UNDERSTOOD, "path missing or not a regular file"));
    }
    File file = path.toFile();
    DicomMediaIO io;
    try {
      io = DicomMediaIO.open(file);
    } catch (Exception e) {
      return closed(shown, reason(NOT_UNDERSTOOD, "cannot parse Part-10"));
    }
    Attributes dcm = io.getDataset();
    String tsUid = io.getTransferSyntax();
    String tsName = TransferSyntax.forUid(tsUid).map(Enum::name).orElse(tsUid);
    String sop = dcm.getString(Tag.SOPClassUID, "");
    String mime = io.mimeType();
    String photo = DicomMediaUtils.photometricInterpretation(dcm);
    Integer rows = dcm.contains(Tag.Rows) ? dcm.getInt(Tag.Rows, 0) : null;
    Integer cols = dcm.contains(Tag.Columns) ? dcm.getInt(Tag.Columns, 0) : null;
    if (isSkippedMime(mime)) {
      return new Verdict(
          shown,
          true,
          tsUid,
          tsName,
          sop,
          mime,
          photo,
          SKIPPED,
          false,
          rows,
          cols,
          null,
          null,
          null,
          reason(NOT_UNDERSTOOD, skippedMimeDetail(mime)));
    }
    if (!DicomUnderstandingLimits.canPaintWindowLevel(tsUid, dcm)) {
      return new Verdict(
          shown,
          true,
          tsUid,
          tsName,
          sop,
          mime,
          photo,
          NOT_UNDERSTOOD_DISPOSITION,
          false,
          rows,
          cols,
          null,
          null,
          null,
          reason(NOT_UNDERSTOOD, unsupportedRasterDetail(tsName, photo)));
    }
    WindLevelParameters wl = DicomMediaUtils.windowLevel(dcm, 400, 40);
    try {
      BufferedImage img = io.paintWindowLevel();
      int w = img.getWidth();
      int h = img.getHeight();
      int corner = img.getRaster().getSample(0, 0, 0);
      int center = img.getRaster().getSample(Math.max(0, w / 2), Math.max(0, h / 2), 0);
      return new Verdict(
          shown,
          true,
          tsUid,
          tsName,
          sop,
          mime,
          photo,
          ACCEPTED,
          true,
          w,
          h,
          wl.getWindow(),
          wl.getLevel(),
          new Samples(corner, center),
          null);
    } catch (RuntimeException e) {
      return new Verdict(
          shown,
          true,
          tsUid,
          tsName,
          sop,
          mime,
          photo,
          SKIPPED,
          false,
          rows,
          cols,
          wl.getWindow(),
          wl.getLevel(),
          null,
          reason(NOT_UNDERSTOOD, "window/level paint failed"));
    }
  }

  static boolean isSkippedMime(String mime) {
    return mime == null
        || DicomMime.UNREADABLE_DICOM.equals(mime)
        || DicomMime.ENCAP_DICOM.equals(mime)
        || DicomMime.VIDEO_DICOM.equals(mime)
        || DicomMime.PR_DICOM.equals(mime)
        || DicomMime.KO_DICOM.equals(mime)
        || DicomMime.SEG_DICOM.equals(mime);
  }

  static String skippedMimeDetail(String mime) {
    if (mime == null || DicomMime.UNREADABLE_DICOM.equals(mime)) {
      return "unreadable or unknown MIME";
    }
    if (DicomMime.ENCAP_DICOM.equals(mime)) {
      return "encapsulated document (non-raster)";
    }
    return "non-raster MIME " + mime;
  }

  static String unsupportedRasterDetail(String transferSyntax, String photometric) {
    return "only explicit VR LE MONOCHROME2 window/level is implemented (got "
        + transferSyntax
        + ", photometric="
        + photometric
        + ")";
  }

  static String reason(String prefix, String detail) {
    if (detail == null || detail.isBlank()) {
      return prefix;
    }
    return prefix + ": " + detail;
  }

  static Verdict closed(String path, String reason) {
    return new Verdict(
        path, false, null, null, null, null, null, SKIPPED, false, null, null, null, null, null,
        reason);
  }

  /**
   * CLI exit contract for cross-oracle callers: {@code 0} understood, {@code 1} opened but not
   * decoded / not understood, {@code 2} usage error or file not opened.
   */
  public static int run(String[] args, PrintStream out, PrintStream err) {
    if (args == null || args.length < 1 || args[0] == null || args[0].isBlank()) {
      err.println(
          "usage: DicomUnderstandingOracle <part-10-path>  (one JSON verdict on stdout; no PHI)");
      return 2;
    }
    Verdict verdict = evaluate(Path.of(args[0]));
    out.println(verdict.toJson());
    return cliExitCode(verdict);
  }

  public static int cliExitCode(Verdict verdict) {
    if (!verdict.opened()) {
      return 2;
    }
    return verdict.understood() ? 0 : 1;
  }

  public static void main(String[] args) {
    System.exit(run(args, System.out, System.err));
  }

  static void jsonField(StringBuilder sb, boolean first, String key, String value) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(key).append("\":");
    if (value == null) {
      sb.append("null");
    } else {
      sb.append(quote(value));
    }
  }

  static void jsonBool(StringBuilder sb, boolean first, String key, boolean value) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(key).append("\":").append(value);
  }

  static void jsonNumber(StringBuilder sb, boolean first, String key, Number value) {
    if (!first) {
      sb.append(',');
    }
    sb.append('"').append(key).append("\":");
    if (value == null) {
      sb.append("null");
    } else if (value instanceof Double d) {
      sb.append(d);
    } else {
      sb.append(value);
    }
  }

  static String quote(String raw) {
    StringBuilder sb = new StringBuilder(raw.length() + 8);
    sb.append('"');
    for (int i = 0; i < raw.length(); i++) {
      char c = raw.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> {
          if (c < 0x20) {
            sb.append(String.format("\\u%04x", (int) c));
          } else {
            sb.append(c);
          }
        }
      }
    }
    sb.append('"');
    return sb.toString();
  }
}
