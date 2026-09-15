/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr.lut;

import java.util.List;

public final class VolumePreset {

  private final String name;
  private final PresetGroup group;
  private final List<PresetPoint> points;

  public VolumePreset(String name, PresetGroup group, List<PresetPoint> points) {
    this.name = name;
    this.group = group == null ? PresetGroup.OTHER : group;
    this.points = points == null ? List.of() : List.copyOf(points);
  }

  public String getName() {
    return name;
  }

  public PresetGroup getGroup() {
    return group;
  }

  public List<PresetPoint> getPoints() {
    return points;
  }

  public static VolumePreset ctSoftTissue() {
    return new VolumePreset(
        "CT Soft Tissue",
        PresetGroup.CT,
        List.of(
            new PresetPoint(-160, 0, 0, 0, 0),
            new PresetPoint(240, 1, 0.8f, 0.7f, 0.6f)));
  }

  public static VolumePreset ctBone() {
    return new VolumePreset(
        "CT Bone",
        PresetGroup.CT,
        List.of(
            new PresetPoint(100, 0, 0, 0, 0),
            new PresetPoint(800, 1, 1, 0.9f, 0.8f)));
  }
}
