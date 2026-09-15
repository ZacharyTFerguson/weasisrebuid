/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** RT Structure Set: StructureSetROISequence + ROIContourSequence. */
public class StructureSet {

  private final String label;
  private final List<StructRegion> regions;

  public StructureSet(String label, List<StructRegion> regions) {
    this.label = label == null ? "" : label;
    this.regions = regions == null ? List.of() : List.copyOf(regions);
  }

  public static StructureSet from(Attributes dataset) {
    if (dataset == null) {
      return new StructureSet("", List.of());
    }
    String label =
        firstNonBlank(
            dataset.getString(Tag.StructureSetLabel), dataset.getString(Tag.StructureSetName));
    Map<Integer, StructRegion> byNumber = new LinkedHashMap<>();
    Sequence rois = dataset.getSequence(Tag.StructureSetROISequence);
    if (rois != null) {
      for (Attributes roi : rois) {
        int number = roi.getInt(Tag.ROINumber, byNumber.size() + 1);
        String name = roi.getString(Tag.ROIName, "ROI " + number);
        byNumber.put(number, new StructRegion(number, name, Color.RED));
      }
    }
    Sequence contours = dataset.getSequence(Tag.ROIContourSequence);
    if (contours != null) {
      for (Attributes item : contours) {
        int number = item.getInt(Tag.ReferencedROINumber, 0);
        Color color = rgb(item.getInts(Tag.ROIDisplayColor));
        StructRegion region = byNumber.get(number);
        if (region == null) {
          region = new StructRegion(number, "ROI " + number, color);
          byNumber.put(number, region);
        } else {
          region.setColor(color);
        }
        Sequence seq = item.getSequence(Tag.ContourSequence);
        if (seq != null) {
          for (Attributes contour : seq) {
            region.addContour(StructContour.from(contour));
          }
        }
      }
    }
    return new StructureSet(label, new ArrayList<>(byNumber.values()));
  }

  static Color rgb(int[] rgb) {
    if (rgb == null || rgb.length < 3) {
      return Color.RED;
    }
    return new Color(clamp(rgb[0]), clamp(rgb[1]), clamp(rgb[2]));
  }

  static int clamp(int v) {
    return Math.max(0, Math.min(255, v));
  }

  static String firstNonBlank(String... values) {
    if (values == null) {
      return "";
    }
    for (String v : values) {
      if (v != null && !v.isBlank()) {
        return v;
      }
    }
    return "";
  }

  public String label() {
    return label;
  }

  public List<StructRegion> regions() {
    return Collections.unmodifiableList(regions);
  }

  public StructRegion region(int number) {
    for (StructRegion region : regions) {
      if (region.number() == number) {
        return region;
      }
    }
    return null;
  }
}
