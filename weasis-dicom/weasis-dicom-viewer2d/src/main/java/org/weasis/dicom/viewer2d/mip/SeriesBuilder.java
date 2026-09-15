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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.weasis.dicom.viewer2d.mpr.MprAxis;
import org.weasis.dicom.viewer2d.mpr.RawImageIO;
import org.weasis.dicom.viewer2d.mpr.Volume;

/**
 * Builds a MIP series: one axial frame per Z from a {@link Volume} and {@link MipView.Type} (None /
 * Min / Mean / Max). Thickness is the sliding slab used by {@link Volume#slice}.
 */
public class SeriesBuilder {

  public List<RawImageIO> buildFrames(Volume volume, MipView.Type type) {
    return buildFrames(volume, type, 1);
  }

  public List<RawImageIO> buildFrames(Volume volume, MipView mip) {
    if (mip == null) {
      return buildFrames(volume, MipView.Type.NONE, 1);
    }
    return buildFrames(volume, mip.getType(), mip.getThickness());
  }

  public List<RawImageIO> buildFrames(Volume volume, MipView.Type type, int thickness) {
    if (volume == null) {
      return List.of();
    }
    MipView.Type mip = type == null ? MipView.Type.NONE : type;
    int slab = Math.max(1, thickness);
    List<RawImageIO> frames = new ArrayList<>(volume.sizeZ());
    for (int z = 0; z < volume.sizeZ(); z++) {
      RawImageIO frame = new RawImageIO();
      frame.setSamples(volume.slice(MprAxis.AXIAL, z, mip, slab));
      frames.add(frame);
    }
    return Collections.unmodifiableList(frames);
  }

  public MipSeries build(Volume volume, MipView.Type type) {
    return build(volume, type, 1);
  }

  public MipSeries build(Volume volume, MipView mip) {
    if (mip == null) {
      return build(volume, MipView.Type.NONE, 1);
    }
    return new MipSeries(mip.getType(), mip.getThickness(), buildFrames(volume, mip));
  }

  public MipSeries build(Volume volume, MipView.Type type, int thickness) {
    MipView.Type mip = type == null ? MipView.Type.NONE : type;
    int slab = Math.max(1, thickness);
    return new MipSeries(mip, slab, buildFrames(volume, mip, slab));
  }

  /** One MIP series: projection type, slab thickness, and the derived frames. */
  public static final class MipSeries {
    private final MipView.Type type;
    private final int thickness;
    private final List<RawImageIO> frames;

    MipSeries(MipView.Type type, int thickness, List<RawImageIO> frames) {
      this.type = type == null ? MipView.Type.NONE : type;
      this.thickness = Math.max(1, thickness);
      this.frames = frames == null ? List.of() : frames;
    }

    public MipView.Type type() {
      return type;
    }

    public int thickness() {
      return thickness;
    }

    public List<RawImageIO> frames() {
      return frames;
    }

    public int size() {
      return frames.size();
    }

    public RawImageIO frame(int index) {
      return frames.get(index);
    }
  }
}
