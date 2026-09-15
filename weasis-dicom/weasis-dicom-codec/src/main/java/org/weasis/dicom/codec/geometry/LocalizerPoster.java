/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.geometry;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public abstract class LocalizerPoster {
  protected final GeometryOfSlice localizerGeometry;

  protected LocalizerPoster(GeometryOfSlice localizerGeometry) {
    this.localizerGeometry = localizerGeometry;
  }

  public GeometryOfSlice getLocalizerGeometry() {
    return localizerGeometry;
  }

  public abstract List<Point2D.Double> getOutlineOnLocalizerForThisGeometry(GeometryOfSlice src);
}
