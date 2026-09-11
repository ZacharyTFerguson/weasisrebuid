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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.imageio.codec.TransferSyntaxType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class TransferSyntaxTest {

  @ParameterizedTest
  @EnumSource(TransferSyntax.class)
  void everyCataloguedSyntaxHasUid(TransferSyntax ts) {
    assertFalse(ts.uid().isBlank());
    assertEquals(ts, TransferSyntax.forUid(ts.uid()).orElseThrow());
  }

  @Test
  void explicitVrLeIsNativeUncompressedAndWritable() {
    TransferSyntax ts = TransferSyntax.EXPLICIT_VR_LE;
    assertFalse(ts.isEncapsulated());
    assertFalse(ts.isLossy());
    assertFalse(ts.isVideo());
    assertTrue(ts.isWriteYes());
    assertEquals(TransferSyntaxType.NATIVE, ts.toolsType());
  }

  @Test
  void jpegBaselineIsLossyEncapsulatedAndMpegIsVideo() {
    assertTrue(TransferSyntax.JPEG_BASELINE.isLossy());
    assertTrue(TransferSyntax.JPEG_BASELINE.isEncapsulated());
    assertTrue(TransferSyntax.isLossyCompression(TransferSyntax.JPEG_BASELINE.uid()));
    assertTrue(TransferSyntax.MPEG4_AVC.isVideo());
    assertTrue(TransferSyntax.JPEG2000_PART2.isEncapsulated());
    assertFalse(TransferSyntax.JPEG2000_PART2.isWriteYes());
    assertTrue(TransferSyntax.HTJ2K.isLossy());
    assertFalse(TransferSyntax.HTJ2K.isWriteYes());
    assertFalse(TransferSyntax.RLE.isLossy());
    assertTrue(TransferSyntax.RLE.isEncapsulated());
  }

  @Test
  void jpegProcess68And1012AreReadOnly() {
    assertFalse(TransferSyntax.JPEG_SPECTRAL_6_8.isWriteYes());
    assertFalse(TransferSyntax.JPEG_PROGRESSIVE_10_12.isWriteYes());
    assertEquals("1.2.840.10008.1.2.4.53", TransferSyntax.JPEG_SPECTRAL_6_8.uid());
    assertEquals("1.2.840.10008.1.2.4.55", TransferSyntax.JPEG_PROGRESSIVE_10_12.uid());
  }
}
