/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model;

import java.util.List;

/** KO/PR referenced series: Series Instance UID plus referenced SOP images. */
public class ReferencedSeries {

  private final String seriesUid;
  private final List<ReferencedImage> images;

  public ReferencedSeries(String seriesUid, List<ReferencedImage> images) {
    this.seriesUid = seriesUid == null ? "" : seriesUid;
    this.images = images == null ? List.of() : List.copyOf(images);
  }

  public String seriesUid() {
    return seriesUid;
  }

  public List<ReferencedImage> images() {
    return images;
  }

  public boolean containsSop(String sop) {
    return findSop(sop == null ? "" : sop);
  }

  boolean findSop(String sop) {
    for (ReferencedImage image : images) {
      if (image.sopInstanceUid().equals(sop)) {
        return true;
      }
    }
    return false;
  }
}
