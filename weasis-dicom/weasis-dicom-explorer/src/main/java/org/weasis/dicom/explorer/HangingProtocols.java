/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Default hanging layouts by modality. MG hangs 2×2; CR/DX 1×2; CT/MR/PT stay 1×1. Series order is
 * {@link DicomSorter#SERIES}.
 */
public class HangingProtocols {

  public record Layout(String name, int rows, int columns) {
    public int viewCount() {
      return Math.max(1, rows) * Math.max(1, columns);
    }
  }

  public Layout layoutFor(String modality) {
    String m = modality == null ? "" : modality.trim().toUpperCase(Locale.ROOT);
    return switch (m) {
      case "MG" -> new Layout("MG 2x2", 2, 2);
      case "CR", "DX", "IO", "PX" -> new Layout(m + " 1x2", 1, 2);
      case "US" -> new Layout("US 2x2", 2, 2);
      case "CT", "MR", "PT" -> new Layout(m + " 1x1", 1, 1);
      default -> new Layout(m.isEmpty() ? "DEFAULT 1x1" : m + " 1x1", 1, 1);
    };
  }

  public Layout apply(DicomModel model) {
    return layoutFor(dominantModality(model == null ? List.of() : model.getInstances()));
  }

  public Layout apply(List<ImportedInstance> instances) {
    return layoutFor(dominantModality(instances));
  }

  public List<ImportedInstance> orderSeries(List<ImportedInstance> instances) {
    return DicomSorter.sortSeries(instances == null ? List.of() : instances);
  }

  public String dominantModality(List<ImportedInstance> instances) {
    if (instances == null || instances.isEmpty()) {
      return "";
    }
    Map<String, Integer> counts = new LinkedHashMap<>();
    for (ImportedInstance inst : instances) {
      if (inst == null) {
        continue;
      }
      String m = inst.modality() == null ? "" : inst.modality().toUpperCase(Locale.ROOT);
      counts.merge(m, 1, Integer::sum);
    }
    String best = "";
    int n = -1;
    for (Map.Entry<String, Integer> e : counts.entrySet()) {
      if (e.getValue() > n) {
        n = e.getValue();
        best = e.getKey();
      }
    }
    return best;
  }

  public List<String> seriesUids(List<ImportedInstance> instances) {
    List<String> uids = new ArrayList<>();
    for (ImportedInstance inst : orderSeries(instances)) {
      if (inst != null && !uids.contains(inst.seriesUid())) {
        uids.add(inst.seriesUid());
      }
    }
    return uids;
  }
}
