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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.DataBufferByte;
import org.dcm4che3.data.Attributes;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.codec.utils.SyntheticDicomFixtures;

/**
 * Multi-frame scroll honesty: paint frame {@code N} from the correct slice of {@code PixelData}.
 *
 * <p><b>Tag:</b> (0028,0008) {@code NumberOfFrames} declares how many contiguous row×column rasters
 * are concatenated in (7FE0,0010) {@code PixelData}. Frame {@code f} starts at sample index {@code
 * f · rows · columns} (same layout as PS3.3 for uncompressed multi-frame; we do not parse per-frame
 * functional groups or cine timing in this stretch).
 *
 * <p><b>Why byte/sample offset, not a second file:</b> instance stack paging (slice 4) swaps whole
 * DICOM objects. Showing frame 0 of a multi-frame object while the scroll index advances would lie
 * about which temporal or phase image is displayed. The only honest stack scroll for a single
 * multi-frame instance is to advance the offset inside {@code PixelData}.
 *
 * <p><b>Why fail-closed:</b> frame index outside {@code [0, NumberOfFrames)} or {@code PixelData}
 * shorter than {@code (frame+1)·rows·cols} throws — we do not clamp to frame 0 or wrap, which would
 * repeat anatomy silently.
 *
 * <p><b>Why not copy Weasis cine:</b> upstream ties multiframe to dedicated cine controllers, frame
 * time vectors, and codec-specific fragment assembly. This oracle path is the minimal uncompressed
 * concatenated-frame layout our tests already write; importing cine UI or {@code SeriesComparator}
 * multiframe heuristics would copy behavior we have not proven in tests.
 */
class WindowLevelPainterFrameTest {

  @Test
  void paintFrameOffsetsIntoPixelData() {
    Attributes dcm = SyntheticDicomFixtures.ctMultiframe3Attributes(8, 8);
    byte[] frame0 =
        ((DataBufferByte)
                WindowLevelPainter.paintMonochrome2(dcm, 0, 400, 40).getRaster().getDataBuffer())
            .getData();
    byte[] frame2 =
        ((DataBufferByte)
                WindowLevelPainter.paintMonochrome2(dcm, 2, 400, 40).getRaster().getDataBuffer())
            .getData();
    assertNotEquals(frame0[0], frame2[0]);
    assertNotEquals(frame0, frame2);
  }

  @Test
  void frameZeroMatchesLegacySingleFramePaint() {
    Attributes dcm = SyntheticDicomFixtures.ctMultiframe3Attributes(4, 4);
    byte[] legacy =
        ((DataBufferByte)
                WindowLevelPainter.paintMonochrome2(dcm, 400, 40).getRaster().getDataBuffer())
            .getData();
    byte[] explicit0 =
        ((DataBufferByte)
                WindowLevelPainter.paintMonochrome2(dcm, 0, 400, 40).getRaster().getDataBuffer())
            .getData();
    assertArrayEquals(legacy, explicit0);
  }

  @Test
  void rejectsFrameIndexOutOfRange() {
    Attributes dcm = SyntheticDicomFixtures.ctMultiframe3Attributes(4, 4);
    assertThrows(
        IllegalArgumentException.class, () -> WindowLevelPainter.paintMonochrome2(dcm, 3, 400, 40));
    assertThrows(
        IllegalArgumentException.class,
        () -> WindowLevelPainter.paintMonochrome2(dcm, -1, 400, 40));
  }
}
