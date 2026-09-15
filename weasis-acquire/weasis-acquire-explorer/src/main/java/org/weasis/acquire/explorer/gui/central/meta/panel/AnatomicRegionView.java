/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta.panel;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireImageInfo;

/** Body-part region picker. Writes code + label onto image and series metadata. */
public class AnatomicRegionView extends JPanel {

  public static final String CODE_TAG = "AnatomicRegionCode";
  public static final String LABEL_TAG = "AnatomicRegion";
  public static final String BODY_PART_TAG = "BodyPartExamined";

  public record Region(String code, String label) {
    @Override
    public String toString() {
      return label + " (" + code + ")";
    }
  }

  public static final List<Region> REGIONS =
      List.of(
          new Region("HEAD", "Head"),
          new Region("NECK", "Neck"),
          new Region("CHEST", "Chest"),
          new Region("ABDOMEN", "Abdomen"),
          new Region("PELVIS", "Pelvis"),
          new Region("SPINE", "Spine"),
          new Region("SHOULDER", "Shoulder"),
          new Region("HAND", "Hand"),
          new Region("KNEE", "Knee"),
          new Region("FOOT", "Foot"));

  private final JComboBox<Region> combo = new JComboBox<>(REGIONS.toArray(Region[]::new));

  public AnatomicRegionView() {
    super(new BorderLayout());
    combo.setName("anatomicRegion");
    add(combo, BorderLayout.CENTER);
  }

  public JComboBox<Region> combo() {
    return combo;
  }

  public Region selected() {
    Region region = (Region) combo.getSelectedItem();
    return region == null ? REGIONS.get(0) : region;
  }

  public void setSelectedCode(String code) {
    if (code == null || code.isBlank()) {
      combo.setSelectedIndex(0);
      return;
    }
    for (int i = 0; i < REGIONS.size(); i++) {
      if (code.equalsIgnoreCase(REGIONS.get(i).code())
          || code.equalsIgnoreCase(REGIONS.get(i).label())) {
        combo.setSelectedIndex(i);
        return;
      }
    }
    combo.setSelectedIndex(0);
  }

  public String cellValue(String tag) {
    Region region = selected();
    if (LABEL_TAG.equals(tag)) {
      return region.label();
    }
    return region.code();
  }

  public void apply(AcquireImageInfo image, Map<String, String> seriesMeta) {
    Region region = selected();
    if (image != null) {
      image.setAnatomicRegionCode(region.code());
      image.setAnatomicRegionLabel(region.label());
    }
    if (seriesMeta != null) {
      seriesMeta.put(CODE_TAG, region.code());
      seriesMeta.put(LABEL_TAG, region.label());
      seriesMeta.put(BODY_PART_TAG, region.code());
    }
  }
}
