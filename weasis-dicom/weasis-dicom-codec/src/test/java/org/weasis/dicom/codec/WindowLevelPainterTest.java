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
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class WindowLevelPainterTest {

  @Test
  void rejectsNullOrNonMonochrome2() {
    assertThrows(IllegalArgumentException.class, () -> WindowLevelPainter.paintMonochrome2(null));
    Attributes rgb = new Attributes();
    rgb.setString(Tag.PhotometricInterpretation, VR.CS, "RGB");
    assertThrows(IllegalArgumentException.class, () -> WindowLevelPainter.paintMonochrome2(rgb));
  }

  @Test
  void rejectsMissingOrShortPixelData() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.Rows, VR.US, 4);
    dcm.setInt(Tag.Columns, VR.US, 4);
    assertThrows(IllegalArgumentException.class, () -> WindowLevelPainter.paintMonochrome2(dcm));
    dcm.setInt(Tag.PixelData, VR.OW, new int[] {1, 2, 3});
    assertThrows(IllegalArgumentException.class, () -> WindowLevelPainter.paintMonochrome2(dcm));
  }

  @Test
  void paintsWithExplicitWindowLevel() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.Rows, VR.US, 2);
    dcm.setInt(Tag.Columns, VR.US, 2);
    dcm.setInt(Tag.PixelRepresentation, VR.US, 1);
    dcm.setInt(Tag.PixelData, VR.OW, new int[] {0, 100, 200, 300});
    var img = WindowLevelPainter.paintMonochrome2(dcm, 400, 40);
    assertEquals(2, img.getWidth());
    assertEquals(2, img.getHeight());
  }
}
