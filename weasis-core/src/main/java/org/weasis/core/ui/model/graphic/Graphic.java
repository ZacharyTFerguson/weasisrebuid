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

import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.core.ui.model.utils.UUIDable;

public interface Graphic extends UUIDable {

  Integer getPtsNumber();

  List<Point2D.Double> getPts();

  void setPts(List<Point2D.Double> pts);

  Boolean getFilled();

  void setFilled(Boolean filled);

  Paint getColorPaint();

  void setColorPaint(Paint paint);

  Float getLineThickness();

  void setLineThickness(Float thickness);

  Boolean getSelected();

  void setSelected(Boolean selected);

  Boolean getLabelVisible();

  void setLabelVisible(Boolean visible);

  String[] getLabel();

  void setLabel(String[] label);

  Shape getShape();

  void buildShape();

  Graphic copy();

  default boolean isMeasurement() {
    GraphicKind kind = GraphicKind.of(this);
    return kind == null || kind.measurement();
  }
}
