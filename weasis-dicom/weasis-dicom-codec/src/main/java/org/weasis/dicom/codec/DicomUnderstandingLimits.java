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

import org.dcm4che3.data.Attributes;
import org.weasis.dicom.codec.utils.DicomMediaUtils;

/**
 * Single gate for pixel understanding in this rebuild. {@link DicomMediaIO#paintWindowLevel()},
 * {@link WindowLevelPainter}, View2d W/L tests, and {@link DicomUnderstandingOracle} must agree
 * with {@link #canPaintWindowLevel(String, Attributes)} — do not duplicate checks elsewhere.
 *
 * <p>Today: <strong>uncompressed</strong> Explicit VR Little Endian, Photometric Interpretation
 * MONOCHROME2, native {@code PixelData} in the dataset (no encapsulated fragments). Everything else
 * is out of scope until a WP proves a wider path with tests.
 */
public final class DicomUnderstandingLimits {

  /**
   * Transfer syntax UID for the only understood pixel encoding (see {@link
   * TransferSyntax#EXPLICIT_VR_LE}).
   */
  public static final String UNDERSTOOD_TRANSFER_SYNTAX_UID = TransferSyntax.EXPLICIT_VR_LE.uid();

  private DicomUnderstandingLimits() {}

  /**
   * True when this Part-10 object may be window-leveled through the Java paint path (oracle +
   * View2d).
   */
  public static boolean canPaintWindowLevel(String transferSyntaxUid, Attributes dataset) {
    return isUncompressedExplicitVrLe(transferSyntaxUid) && DicomMediaUtils.isMonochrome2(dataset);
  }

  /** Explicit VR LE and not an encapsulated compression transfer syntax. */
  public static boolean isUncompressedExplicitVrLe(String transferSyntaxUid) {
    if (transferSyntaxUid == null || transferSyntaxUid.isBlank()) {
      return false;
    }
    if (!UNDERSTOOD_TRANSFER_SYNTAX_UID.equals(transferSyntaxUid.trim())) {
      return false;
    }
    return TransferSyntax.forUid(transferSyntaxUid).map(ts -> !ts.isEncapsulated()).orElse(false);
  }
}
