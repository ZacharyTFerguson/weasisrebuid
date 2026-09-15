/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Info-overlay items (patient, study, W/L, …) shown when annotations are FULL. */
public class LayerAnnotation extends Layer {

  public static final String PATIENT = "patient";
  public static final String STUDY = "study";
  public static final String SERIES = "series";
  public static final String WINDOW_LEVEL = "windowLevel";
  public static final String ORIENTATION = "orientation";

  private final Map<String, Boolean> items = new LinkedHashMap<>();

  public LayerAnnotation() {
    setType(LayerType.ANNOTATION);
    items.put(PATIENT, Boolean.TRUE);
    items.put(STUDY, Boolean.TRUE);
    items.put(SERIES, Boolean.TRUE);
    items.put(WINDOW_LEVEL, Boolean.TRUE);
    items.put(ORIENTATION, Boolean.TRUE);
  }

  public boolean isItemVisible(String key) {
    if (key == null) {
      return false;
    }
    return Boolean.TRUE.equals(items.getOrDefault(key, Boolean.TRUE));
  }

  public void setItemVisible(String key, boolean visible) {
    if (key != null && !key.isBlank()) {
      items.put(key, visible);
    }
  }

  public Set<String> keys() {
    return Set.copyOf(items.keySet());
  }
}
