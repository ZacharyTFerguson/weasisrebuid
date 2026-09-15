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

import java.awt.geom.Area;
import java.util.List;

public class VolumeSliceContourLoader implements LazyContourLoader {
  private final SegmentationVolume volume;
  private final int segmentNumber;

  public VolumeSliceContourLoader(SegmentationVolume volume, int segmentNumber) {
    this.volume = volume;
    this.segmentNumber = segmentNumber;
  }

  public int getSegmentNumber() {
    return segmentNumber;
  }

  @Override
  public List<Area> getContours(int frame) {
    return volume == null ? List.of() : volume.contours(segmentNumber, frame);
  }
}
