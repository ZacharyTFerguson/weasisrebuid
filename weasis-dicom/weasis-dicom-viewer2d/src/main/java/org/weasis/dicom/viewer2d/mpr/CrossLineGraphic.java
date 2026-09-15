/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.layer.LayerType;

/**
 * MPR crosshair line on a plane. Thickness &gt; 1 draws the MIP slab as a pair of parallel lines
 * (Alt+wheel on the selected axis).
 */
public class CrossLineGraphic extends LineGraphic {

  private MprAxis axis = MprAxis.AXIAL;
  private int thickness = 1;
  private boolean horizontal = true;

  public CrossLineGraphic() {
    setColorPaint(colorFor(axis));
  }

  public CrossLineGraphic(MprAxis axis, boolean horizontal) {
    this();
    setAxis(axis);
    this.horizontal = horizontal;
  }

  public static Color colorFor(MprAxis axis) {
    if (axis == MprAxis.CORONAL) {
      return Color.GREEN;
    }
    if (axis == MprAxis.SAGITTAL) {
      return Color.RED;
    }
    return Color.BLUE;
  }

  /** Horizontal line = next axial/coronal partner; vertical = remaining axis. */
  public static MprAxis horizontalAxis(MprAxis view) {
    if (view == MprAxis.AXIAL) {
      return MprAxis.CORONAL;
    }
    return MprAxis.AXIAL;
  }

  public static MprAxis verticalAxis(MprAxis view) {
    if (view == MprAxis.SAGITTAL) {
      return MprAxis.CORONAL;
    }
    return MprAxis.SAGITTAL;
  }

  public static CrossLineGraphic[] forView(
      MprAxis view, double cx, double cy, double width, double height, int thickness) {
    MprAxis plane = view == null ? MprAxis.AXIAL : view;
    CrossLineGraphic across = new CrossLineGraphic(horizontalAxis(plane), true);
    across.setThickness(thickness);
    across.span(width, height, cx, cy);
    CrossLineGraphic down = new CrossLineGraphic(verticalAxis(plane), false);
    down.setThickness(thickness);
    down.span(width, height, cx, cy);
    return new CrossLineGraphic[] {across, down};
  }

  public MprAxis getAxis() {
    return axis;
  }

  public void setAxis(MprAxis axis) {
    this.axis = axis == null ? MprAxis.AXIAL : axis;
    setColorPaint(colorFor(this.axis));
  }

  public int getThickness() {
    return thickness;
  }

  public void setThickness(int thickness) {
    this.thickness = Math.max(1, thickness);
    buildShape();
  }

  public boolean isHorizontal() {
    return horizontal;
  }

  public void setHorizontal(boolean horizontal) {
    this.horizontal = horizontal;
  }

  public LayerType getLayerType() {
    return LayerType.CROSSLINES;
  }

  public void span(double width, double height, double cx, double cy) {
    double maxX = Math.max(0, width - 1);
    double maxY = Math.max(0, height - 1);
    if (horizontal) {
      setHandlePoint(0, new Point2D.Double(0, cy));
      setHandlePoint(1, new Point2D.Double(maxX, cy));
    } else {
      setHandlePoint(0, new Point2D.Double(cx, 0));
      setHandlePoint(1, new Point2D.Double(cx, maxY));
    }
  }

  public double slabWidth() {
    if (thickness <= 1) {
      return 0;
    }
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      return thickness;
    }
    return offsetDistance();
  }

  private double offsetDistance() {
    return thickness / 2.0;
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      setShape(null);
      return;
    }
    if (thickness <= 1) {
      setShape(new Line2D.Double(a, b));
      return;
    }
    double dx = b.x - a.x;
    double dy = b.y - a.y;
    double len = Math.hypot(dx, dy);
    if (len == 0) {
      setShape(new Line2D.Double(a, b));
      return;
    }
    double half = offsetDistance();
    double nx = -dy / len * half;
    double ny = dx / len * half;
    Path2D path = new Path2D.Double();
    path.moveTo(a.x + nx, a.y + ny);
    path.lineTo(b.x + nx, b.y + ny);
    path.moveTo(a.x - nx, a.y - ny);
    path.lineTo(b.x - nx, b.y - ny);
    setShape(path);
  }

  @Override
  public Graphic copy() {
    CrossLineGraphic copy = (CrossLineGraphic) super.copy();
    copy.setAxis(axis);
    copy.setHorizontal(horizontal);
    copy.setThickness(thickness);
    return copy;
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new CrossLineGraphic();
  }
}
