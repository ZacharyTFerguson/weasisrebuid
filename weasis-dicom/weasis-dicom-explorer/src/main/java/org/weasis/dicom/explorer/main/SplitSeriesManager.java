/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.weasis.dicom.codec.utils.SplittingRules;
import org.weasis.dicom.explorer.ImportedInstance;

/**
 * Rewrites Series Instance UID when a splitting modality (MR / CT / PT / NM) mixes echo, temporal
 * position, or contrast inside one UID. Non-splitting modalities and single-key groups stay as
 * imported.
 */
public class SplitSeriesManager {

  private final SplittingRules rules;

  public SplitSeriesManager() {
    this(new SplittingRules());
  }

  public SplitSeriesManager(SplittingRules rules) {
    this.rules = rules == null ? new SplittingRules() : rules;
  }

  public List<ImportedInstance> rewrite(List<ImportedInstance> instances) {
    if (instances == null || instances.isEmpty()) {
      return List.of();
    }
    List<ImportedInstance> out = new ArrayList<>();
    for (List<ImportedInstance> group : buckets(instances).values()) {
      out.addAll(rewriteGroup(group));
    }
    return List.copyOf(out);
  }

  Map<String, List<ImportedInstance>> buckets(List<ImportedInstance> instances) {
    Map<String, List<ImportedInstance>> map = new LinkedHashMap<>();
    for (ImportedInstance inst : instances) {
      if (inst != null) {
        map.computeIfAbsent(inst.seriesUid(), k -> new ArrayList<>()).add(inst);
      }
    }
    return map;
  }

  List<ImportedInstance> rewriteGroup(List<ImportedInstance> group) {
    if (!shouldSplitGroup(group) || distinctKeys(group) <= 1) {
      return group;
    }
    return rewriteKeyed(group);
  }

  boolean shouldSplitGroup(List<ImportedInstance> group) {
    for (ImportedInstance inst : group) {
      if (rules.shouldSplit(inst.modality())) {
        return true;
      }
    }
    return false;
  }

  static int distinctKeys(List<ImportedInstance> group) {
    LinkedHashSet<String> keys = new LinkedHashSet<>();
    for (ImportedInstance inst : group) {
      keys.add(splitKey(inst));
    }
    return keys.size();
  }

  static String splitKey(ImportedInstance inst) {
    return inst.echoNumber() + "\t" + inst.temporalPosition() + "\t" + inst.contrastAgent();
  }

  static List<ImportedInstance> rewriteKeyed(List<ImportedInstance> group) {
    List<ImportedInstance> out = new ArrayList<>();
    for (ImportedInstance inst : group) {
      out.add(inst.withSeriesUid(inst.seriesUid() + "." + suffixFor(splitKey(inst))));
    }
    return out;
  }

  static String suffixFor(String key) {
    String[] parts = key.split("\t", -1);
    StringBuilder sb = new StringBuilder();
    appendPart(sb, "echo", parts, 0);
    appendPart(sb, "t", parts, 1);
    appendPart(sb, "c", parts, 2);
    return sb.isEmpty() ? "base" : sb.toString();
  }

  static void appendPart(StringBuilder sb, String prefix, String[] parts, int index) {
    if (index >= parts.length || parts[index].isBlank()) {
      return;
    }
    if (!sb.isEmpty()) {
      sb.append('.');
    }
    sb.append(prefix).append(safeToken(parts[index]));
  }

  static String safeToken(String raw) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < raw.length(); i++) {
      appendLetterOrDigit(sb, raw.charAt(i));
    }
    return sb.isEmpty() ? "x" : sb.toString();
  }

  static void appendLetterOrDigit(StringBuilder sb, char c) {
    if (Character.isLetterOrDigit(c)) {
      sb.append(c);
    }
  }
}
