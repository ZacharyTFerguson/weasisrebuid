/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.image.measure.MeasurementsAdapter;
import org.weasis.core.api.image.util.Unit;

class FusionMeasurableLayerHaveTest {

  @Test
  void overlayStackProvidesMillimeterAdapterWhenContentPresent() {
    FusionStack stack =
        new FusionVolumeBuilder()
            .build(
                new Attributes[] {axial("1.2.FOR", 0.0)},
                new double[][][] {new double[][] {{1, 1}, {1, 1}}});
    FusionMeasurableLayer layer = new FusionMeasurableLayer(stack, 0.5);
    assertTrue(layer.hasContent());
    MeasurementsAdapter mm = layer.getMeasurementAdapter(Unit.MILLIMETER);
    assertEquals(5.0, mm.getLength(10), 1e-9);
    assertEquals(Unit.MILLIMETER, mm.getUnit());
    MeasurementsAdapter px = layer.getMeasurementAdapter(Unit.PIXEL);
    assertEquals(10.0, px.getLength(10), 1e-9);
    assertFalse(new FusionMeasurableLayer().hasContent());
  }

  private static Attributes axial(String forUid, double z) {
    Attributes a = new Attributes();
    a.setString(Tag.FrameOfReferenceUID, VR.UI, forUid);
    a.setDouble(Tag.ImagePositionPatient, VR.DS, 0.0, 0.0, z);
    a.setDouble(Tag.ImageOrientationPatient, VR.DS, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    a.setDouble(Tag.PixelSpacing, VR.DS, 0.5, 0.5);
    a.setInt(Tag.Rows, VR.US, 2);
    a.setInt(Tag.Columns, VR.US, 2);
    return a;
  }
}
