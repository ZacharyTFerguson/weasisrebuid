/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.display;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.weasis.core.api.media.data.TagView;

public class CornerInfoData {
  private final Map<CornerDisplay, List<TagView>> corners = new EnumMap<>(CornerDisplay.class);

  public CornerInfoData() {
    for (CornerDisplay corner : CornerDisplay.values()) {
      corners.put(corner, new ArrayList<>());
    }
  }

  public List<TagView> getCorner(CornerDisplay corner) {
    return corners.computeIfAbsent(corner, c -> new ArrayList<>());
  }

  public void setCorner(CornerDisplay corner, List<TagView> tags) {
    getCorner(corner).clear();
    if (tags != null) {
      getCorner(corner).addAll(tags);
    }
  }
}
