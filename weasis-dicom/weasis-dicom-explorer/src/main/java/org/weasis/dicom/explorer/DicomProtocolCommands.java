/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.command.DicomCloseArgs;
import org.weasis.core.api.command.DicomGetArgs;
import org.weasis.core.api.command.DicomRsArgs;
import org.weasis.core.api.util.PortableDicomDirs;
import org.weasis.dicom.explorer.mf.ManifestParser;

/**
 * Gogo {@code dicom:get} / {@code dicom:close} / {@code dicom:rs} (same verbs as {@code $dicom:*}
 * without {@code $}).
 */
@Component(
    immediate = true,
    service = DicomProtocolCommands.class,
    property = {
      "osgi.command.scope=dicom",
      "osgi.command.function=get",
      "osgi.command.function=close",
      "osgi.command.function=rs"
    })
public class DicomProtocolCommands {

  private final DicomModel model;

  public DicomProtocolCommands() {
    this(new DicomModel());
  }

  public DicomProtocolCommands(DicomModel model) {
    this.model = model == null ? new DicomModel() : model;
  }

  public DicomModel model() {
    return model;
  }

  public String get(String... args) throws Exception {
    DicomGetArgs parsed = DicomGetArgs.parse(args);
    return switch (parsed.mode()) {
      case LOCAL -> getLocal(parsed.value());
      case MANIFEST -> getManifest(parsed.value(), false);
      case IWADO -> getManifest(parsed.value(), true);
      case ZIP -> getLocal(parsed.value());
      case REMOTE -> "queued " + parsed.mode() + " " + parsed.value();
      case PORTABLE -> getPortable(parsed.value());
      case HELP -> helpGet();
    };
  }

  public String close(String... args) {
    DicomCloseArgs parsed = DicomCloseArgs.parse(args);
    return switch (parsed.mode()) {
      case ALL -> {
        int n = model.getInstances().size();
        model.clear();
        yield "closed " + n;
      }
      case PATIENT, STUDY, SERIES -> "closed " + parsed.mode() + " " + parsed.value();
      case HELP -> helpClose();
    };
  }

  public String rs(String... args) {
    try {
      DicomRsArgs parsed = DicomRsArgs.parse(args);
      return "rs " + parsed.url() + " accept-ext=" + parsed.acceptExt();
    } catch (IllegalArgumentException e) {
      return e.getMessage();
    }
  }

  String getPortable(String root) throws Exception {
    Path base = root == null || root.isBlank() ? null : Path.of(root);
    String csv =
        System.getProperty(PortableDicomDirs.PREF, PortableDicomDirs.DEFAULT);
    List<Path> dirs = PortableDicomDirs.existing(base, csv);
    int imported = 0;
    for (Path dir : dirs) {
      LoadLocalDicom.ImportResult result =
          LoadLocalDicom.importPath(dir.toFile(), null, model, new SkipUnsupportedSopNotifier());
      imported += result.imported().size();
    }
    return "imported " + imported + " portable " + dirs.size();
  }

  String getLocal(String path) throws Exception {
    if (path == null || path.isBlank()) {
      return "dicom:get -l requires a path";
    }
    LoadLocalDicom.ImportResult result =
        LoadLocalDicom.importPath(new File(path), null, model, new SkipUnsupportedSopNotifier());
    return "imported " + result.imported().size();
  }

  String getManifest(String value, boolean gzipBase64) throws Exception {
    if (value == null || value.isBlank()) {
      return "dicom:get -w requires a manifest";
    }
    String body;
    Path asFile = Path.of(value);
    if (Files.isRegularFile(asFile)) {
      body = Files.readString(asFile);
    } else if (gzipBase64) {
      body = gunzip(Base64.getDecoder().decode(value));
    } else {
      body = value;
    }
    List<ManifestParser.ManifestSeries> series =
        body.trim().startsWith("{")
            ? ManifestParser.parseJson(body)
            : ManifestParser.parseXml(body);
    return "manifest " + series.size();
  }

  static String gunzip(byte[] gzip) throws Exception {
    try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(gzip));
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      in.transferTo(out);
      return out.toString(StandardCharsets.UTF_8);
    }
  }

  static String helpGet() {
    return "Usage: dicom:get (-l path | -w manifest | -r URI | -z ZIP | -p portable | -i iwado)";
  }

  static String helpClose() {
    return "Usage: dicom:close (-a | -p ID | -y StudyUID | -s SeriesUID)";
  }
}
