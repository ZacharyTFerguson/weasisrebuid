/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic;

/** Closed-area graphic (rectangle, ellipse, polygon). */
public interface GraphicArea extends DragGraphic {

  double getAreaValue();

  default double getPerimeter() {
    return org.weasis.core.ui.model.graphic.GraphicMath.polygonPerimeter(getPts(), true);
  }

  default double getOmbbArea() {
    return org.weasis.core.ui.model.graphic.GraphicMath.ombbArea(getPts());
  }
}
