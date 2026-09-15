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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Groups closed planar contours by Z (mm) for overlay on a CT slice. */
public class PlaneContourLoader {

  private final Map<KeyDouble, List<StructContour>> byPlane = new LinkedHashMap<>();

  public PlaneContourLoader(StructureSet structureSet) {
    if (structureSet == null) {
      return;
    }
    for (StructRegion region : structureSet.regions()) {
      if (!region.isVisible()) {
        continue;
      }
      for (StructContour contour : region.contours()) {
        byPlane.computeIfAbsent(new KeyDouble(contour.z()), key -> new ArrayList<>()).add(contour);
      }
    }
  }

  public List<StructContour> contoursAt(double z) {
    List<StructContour> found = byPlane.get(new KeyDouble(z));
    return found == null ? List.of() : Collections.unmodifiableList(found);
  }

  public List<KeyDouble> planes() {
    return List.copyOf(byPlane.keySet());
  }
}
