/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import java.awt.geom.Area;
import java.util.List;

public class FractionalContourLoader implements LazyContourLoader {
  private final LazyContourLoader binary;
  private final float threshold;

  public FractionalContourLoader(LazyContourLoader binary, float threshold) {
    this.binary = binary;
    this.threshold = threshold;
  }

  public float getThreshold() {
    return threshold;
  }

  @Override
  public List<Area> getContours(int frame) {
    return binary == null ? List.of() : binary.getContours(frame);
  }
}
