/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import org.weasis.dicom.codec.DcmMediaReader;

public class SegmentationVolumeBuilder {
  private final SegSpecialElement seg;

  public SegmentationVolumeBuilder(SegSpecialElement seg) {
    this.seg = seg;
  }

  public SegSpecialElement getSeg() {
    return seg;
  }

  public SegmentationVolume build() {
    if (seg == null) {
      return new SegmentationVolume();
    }
    DcmMediaReader reader = seg.getMediaReader();
    if (reader != null && reader.getDicomObject() != null) {
      var dcm = reader.getDicomObject();
      int rows = dcm.getInt(org.dcm4che3.data.Tag.Rows, 0);
      int cols = dcm.getInt(org.dcm4che3.data.Tag.Columns, 0);
      int frames = dcm.getInt(org.dcm4che3.data.Tag.NumberOfFrames, 1);
      SegmentationVolume volume = new SegmentationVolume(rows, cols, frames);
      seg.getVolume().putSegment(1, new BasicContourLoader(java.util.List.of()));
      return volume;
    }
    return seg.getVolume();
  }
}
