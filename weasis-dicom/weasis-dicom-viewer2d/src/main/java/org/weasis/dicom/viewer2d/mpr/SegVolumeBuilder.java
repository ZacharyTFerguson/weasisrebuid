/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import org.weasis.dicom.codec.seg.MaskFrames;

/**
 * Rasterizes DICOM SEG frames into a dense MPR volume. Eight-bit label maps become {@link
 * VolumeByte}; sixteen-bit packed little-endian frames become {@link VolumeShort}.
 */
public class SegVolumeBuilder {

  public Volume rasterize(MaskFrames frames) {
    return rasterize(frames, 8);
  }

  public Volume rasterize(MaskFrames frames, int bitsStored) {
    if (bitsStored > 8) {
      return rasterizeShort(frames);
    }
    return rasterizeByte(frames);
  }

  public VolumeByte rasterizeByte(MaskFrames frames) {
    if (frames == null || frames.size() == 0) {
      return new VolumeByte(1, 1, 1);
    }
    int sizeX = Math.max(1, frames.getColumns());
    int sizeY = Math.max(1, frames.getRows());
    int sizeZ = Math.max(1, frames.size());
    VolumeByte volume = new VolumeByte(sizeX, sizeY, sizeZ);
    for (int z = 0; z < sizeZ; z++) {
      copyFrame(volume, frames.getFrame(z), sizeX, sizeY, z);
    }
    return volume;
  }

  public VolumeShort rasterizeShort(MaskFrames frames) {
    if (frames == null || frames.size() == 0) {
      return new VolumeShort(1, 1, 1);
    }
    int sizeX = Math.max(1, frames.getColumns());
    int sizeY = Math.max(1, frames.getRows());
    int sizeZ = Math.max(1, frames.size());
    VolumeShort volume = new VolumeShort(sizeX, sizeY, sizeZ);
    for (int z = 0; z < sizeZ; z++) {
      copyPackedShorts(volume, frames.getFrame(z), sizeX, sizeY, z);
    }
    return volume;
  }

  public VolumeShort rasterizeShort(int rows, int columns, short[][] frames) {
    if (frames == null || frames.length == 0) {
      return new VolumeShort(Math.max(1, columns), Math.max(1, rows), 1);
    }
    int sizeX = Math.max(1, columns);
    int sizeY = Math.max(1, rows);
    int sizeZ = frames.length;
    VolumeShort volume = new VolumeShort(sizeX, sizeY, sizeZ);
    for (int z = 0; z < sizeZ; z++) {
      short[] plane = frames[z];
      if (plane == null) {
        continue;
      }
      int n = Math.min(plane.length, sizeX * sizeY);
      for (int i = 0; i < n; i++) {
        volume.setValue(i % sizeX, i / sizeX, z, plane[i]);
      }
    }
    return volume;
  }

  /** Binary (or occupancy) frames: non-zero pixels receive {@code segmentNumber}. */
  public VolumeByte rasterizeBinary(MaskFrames frames, int segmentNumber) {
    VolumeByte volume = rasterizeByte(frames);
    if (segmentNumber == 0) {
      return volume;
    }
    int label = segmentNumber & 0xff;
    for (int z = 0; z < volume.sizeZ(); z++) {
      for (int y = 0; y < volume.sizeY(); y++) {
        for (int x = 0; x < volume.sizeX(); x++) {
          if (volume.value(x, y, z) != 0) {
            volume.setValue(x, y, z, label);
          }
        }
      }
    }
    return volume;
  }

  private static void copyFrame(Volume volume, byte[] plane, int sizeX, int sizeY, int z) {
    if (plane == null) {
      return;
    }
    int n = Math.min(plane.length, sizeX * sizeY);
    for (int i = 0; i < n; i++) {
      volume.setValue(i % sizeX, i / sizeX, z, plane[i] & 0xff);
    }
  }

  private static void copyPackedShorts(Volume volume, byte[] plane, int sizeX, int sizeY, int z) {
    if (plane == null) {
      return;
    }
    int voxels = sizeX * sizeY;
    for (int i = 0; i < voxels; i++) {
      int off = i * 2;
      if (off + 1 >= plane.length) {
        break;
      }
      int v = (plane[off] & 0xff) | ((plane[off + 1] & 0xff) << 8);
      if (v >= 32768) {
        v -= 65536;
      }
      volume.setValue(i % sizeX, i / sizeX, z, v);
    }
  }
}
