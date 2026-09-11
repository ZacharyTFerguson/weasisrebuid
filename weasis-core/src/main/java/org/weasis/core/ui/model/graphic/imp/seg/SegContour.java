/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.seg;

import java.awt.geom.Path2D;

public class SegContour {

  private Path2D path = new Path2D.Double();
  private SegRegion region = new SegRegion();

  public Path2D getPath() {
    return path;
  }

  public void setPath(Path2D path) {
    this.path = path == null ? new Path2D.Double() : path;
  }

  public SegRegion getRegion() {
    return region;
  }
}
