/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Maps a referenced SOP Instance UID to hidden special elements (PR/KO/SEG). */
public final class HiddenSeriesManager {
  private static final HiddenSeriesManager INSTANCE = new HiddenSeriesManager();
  private final Map<String, List<DicomSpecialElement>> bySop = new ConcurrentHashMap<>();

  private HiddenSeriesManager() {}

  public static HiddenSeriesManager getInstance() {
    return INSTANCE;
  }

  public void register(DicomSpecialElement element) {
    if (element == null) {
      return;
    }
    for (String sop : element.getReferencedSopInstanceUIDs()) {
      bySop.computeIfAbsent(sop, k -> new ArrayList<>()).add(element);
    }
  }

  public List<DicomSpecialElement> getHiddenElementsContainingSopUid(String sopInstanceUid) {
    if (sopInstanceUid == null) {
      return List.of();
    }
    List<DicomSpecialElement> list = bySop.get(sopInstanceUid);
    return list == null ? List.of() : List.copyOf(list);
  }

  public void remove(DicomSpecialElement element) {
    if (element == null) {
      return;
    }
    for (List<DicomSpecialElement> list : bySop.values()) {
      list.remove(element);
    }
  }
}
