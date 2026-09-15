/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.mpr.RawImageIO;
import org.weasis.dicom.viewer2d.mpr.VolumeShort;

class SeriesBuilderHaveTest {

  @Test
  void buildsMipSeriesFramesFromVolumeAndType() {
    VolumeShort vol = new VolumeShort(1, 1, 3);
    vol.setValue(0, 0, 0, 1);
    vol.setValue(0, 0, 1, 8);
    vol.setValue(0, 0, 2, 3);

    SeriesBuilder.MipSeries none = new SeriesBuilder().build(vol, MipView.Type.NONE);
    assertEquals(MipView.Type.NONE, none.type());
    assertEquals(3, none.size());
    assertEquals(1.0, none.frame(0).samples()[0][0], 1e-9);
    assertEquals(8.0, none.frame(1).samples()[0][0], 1e-9);
    assertEquals(3.0, none.frame(2).samples()[0][0], 1e-9);

    SeriesBuilder.MipSeries max = new SeriesBuilder().build(vol, MipView.Type.MAX, 3);
    assertEquals(MipView.Type.MAX, max.type());
    assertEquals(3, max.thickness());
    assertEquals(8.0, max.frame(1).samples()[0][0], 1e-9);
    assertEquals(8.0, max.frame(0).samples()[0][0], 1e-9);

    List<RawImageIO> min = new SeriesBuilder().buildFrames(vol, MipView.Type.MIN, 3);
    assertEquals(1.0, min.get(1).samples()[0][0], 1e-9);

    MipView mip = new MipView();
    mip.setType(MipView.Type.MEAN);
    mip.setThickness(3);
    List<RawImageIO> mean = new SeriesBuilder().buildFrames(vol, mip);
    assertEquals(4.0, mean.get(1).samples()[0][0], 1e-9);
    assertTrue(new SeriesBuilder().buildFrames(null, MipView.Type.MAX).isEmpty());
  }
}
