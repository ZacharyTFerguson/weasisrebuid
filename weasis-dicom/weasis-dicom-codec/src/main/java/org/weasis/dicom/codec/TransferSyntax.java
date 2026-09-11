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

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import org.dcm4che3.imageio.codec.TransferSyntaxType;

/** SRS §4.13 / CHECKLIST §4.12 transfer syntax catalog (UID + encapsulation / lossy / write). */
public enum TransferSyntax {
  IMPLICIT_VR_LE("1.2.840.10008.1.2", false, false, true),
  EXPLICIT_VR_LE("1.2.840.10008.1.2.1", false, false, true),
  DEFLATED_EXPLICIT_VR_LE("1.2.840.10008.1.2.1.99", false, false, true),
  EXPLICIT_VR_BE("1.2.840.10008.1.2.2", false, false, false),
  RLE("1.2.840.10008.1.2.5", true, false, true),
  JPEG_BASELINE("1.2.840.10008.1.2.4.50", true, true, true),
  JPEG_EXTENDED("1.2.840.10008.1.2.4.51", true, true, true),
  JPEG_SPECTRAL_6_8("1.2.840.10008.1.2.4.53", true, true, false),
  JPEG_PROGRESSIVE_10_12("1.2.840.10008.1.2.4.55", true, true, false),
  JPEG_LOSSLESS_PROCESS_14("1.2.840.10008.1.2.4.57", true, false, true),
  JPEG_LOSSLESS_SV1("1.2.840.10008.1.2.4.70", true, false, true),
  JPEG_LS_LOSSLESS("1.2.840.10008.1.2.4.80", true, false, true),
  JPEG_LS_NEAR_LOSSLESS("1.2.840.10008.1.2.4.81", true, true, true),
  JPEG2000_LOSSLESS("1.2.840.10008.1.2.4.90", true, false, true),
  JPEG2000("1.2.840.10008.1.2.4.91", true, true, true),
  JPEG2000_PART2_LOSSLESS("1.2.840.10008.1.2.4.92", true, false, false),
  JPEG2000_PART2("1.2.840.10008.1.2.4.93", true, true, false),
  JPEG_XL_LOSSLESS("1.2.840.10008.1.2.4.110", true, false, true),
  JPEG_XL_JPEG_RECOMPRESSION("1.2.840.10008.1.2.4.111", true, true, true),
  JPEG_XL("1.2.840.10008.1.2.4.112", true, true, true),
  HTJ2K_LOSSLESS("1.2.840.10008.1.2.4.201", true, false, false),
  HTJ2K_RPCL("1.2.840.10008.1.2.4.202", true, false, false),
  HTJ2K("1.2.840.10008.1.2.4.203", true, true, false),
  MPEG2_ML("1.2.840.10008.1.2.4.100", true, true, false),
  MPEG2_HL("1.2.840.10008.1.2.4.101", true, true, false),
  MPEG4_AVC("1.2.840.10008.1.2.4.102", true, true, false),
  MPEG4_AVC_BD("1.2.840.10008.1.2.4.103", true, true, false),
  HEVC("1.2.840.10008.1.2.4.107", true, true, false),
  HEVC_10("1.2.840.10008.1.2.4.108", true, true, false),
  RFC2557_MIME("1.2.840.10008.1.2.6.1", true, false, false);

  private final String uid;
  private final boolean encapsulated;
  private final boolean lossy;
  private final boolean writeYes;

  TransferSyntax(String uid, boolean encapsulated, boolean lossy, boolean writeYes) {
    this.uid = uid;
    this.encapsulated = encapsulated;
    this.lossy = lossy;
    this.writeYes = writeYes;
  }

  public String uid() {
    return uid;
  }

  public boolean isEncapsulated() {
    return encapsulated;
  }

  public boolean isLossy() {
    return lossy;
  }

  /** {@code false} means read-only (Process 6&amp;8 / 10&amp;12, JP2K Part 2, HTJ2K, video). */
  public boolean isWriteYes() {
    return writeYes;
  }

  public boolean isVideo() {
    return this == MPEG2_ML
        || this == MPEG2_HL
        || this == MPEG4_AVC
        || this == MPEG4_AVC_BD
        || this == HEVC
        || this == HEVC_10;
  }

  public static Optional<TransferSyntax> forUid(String uid) {
    if (uid == null || uid.isBlank()) {
      return Optional.empty();
    }
    String trimmed = uid.trim();
    return Arrays.stream(values()).filter(ts -> ts.uid.equals(trimmed)).findFirst();
  }

  public static boolean isKnown(String uid) {
    return forUid(uid).isPresent();
  }

  public static boolean isLossyCompression(String uid) {
    Optional<TransferSyntax> ours = forUid(uid);
    if (ours.isPresent()) {
      return ours.get().isLossy();
    }
    return TransferSyntaxType.isLossyCompression(uid);
  }

  public boolean matches(String otherUid) {
    if (otherUid == null) {
      return false;
    }
    return uid.equals(otherUid.trim())
        || uid.equalsIgnoreCase(otherUid.trim().toLowerCase(Locale.ROOT));
  }

  /** Cross-check against weasis-dicom-tools {@link TransferSyntaxType}. */
  public TransferSyntaxType toolsType() {
    return TransferSyntaxType.forUID(uid);
  }
}
