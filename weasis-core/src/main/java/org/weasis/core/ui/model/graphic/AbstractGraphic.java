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

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractGraphic implements Graphic {

  private String uuid = UUID.randomUUID().toString();
  private final List<Point2D.Double> pts = new ArrayList<>();
  private Boolean filled = Boolean.FALSE;
  private Paint colorPaint = Color.YELLOW;
  private Float lineThickness = 1.0f;
  private Boolean selected = Boolean.FALSE;
  private Boolean labelVisible = Boolean.TRUE;
  private String[] label = new String[0];
  private Shape shape;

  /** Fill opacity relative to line alpha (CHECKLIST §4.4). Default 1.0. */
  private float fillOpacity = 1.0f;

  private float lineAlpha = 1.0f;

  protected AbstractGraphic(int expectedPts) {
    for (int i = 0; i < expectedPts; i++) {
      pts.add(new Point2D.Double(0, 0));
    }
  }

  @Override
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    this.uuid = uuid == null ? this.uuid : uuid;
  }

  @Override
  public Integer getPtsNumber() {
    return pts.size();
  }

  @Override
  public List<Point2D.Double> getPts() {
    return Collections.unmodifiableList(pts);
  }

  @Override
  public void setPts(List<Point2D.Double> newPts) {
    pts.clear();
    if (newPts != null) {
      for (Point2D.Double p : newPts) {
        pts.add(p == null ? new Point2D.Double() : new Point2D.Double(p.getX(), p.getY()));
      }
    }
    buildShape();
  }

  protected List<Point2D.Double> ptsMutable() {
    return pts;
  }

  @Override
  public Boolean getFilled() {
    return filled;
  }

  @Override
  public void setFilled(Boolean filled) {
    this.filled = filled != null && filled;
  }

  @Override
  public Paint getColorPaint() {
    return colorPaint;
  }

  @Override
  public void setColorPaint(Paint paint) {
    this.colorPaint = paint == null ? Color.YELLOW : paint;
  }

  @Override
  public Float getLineThickness() {
    return lineThickness;
  }

  @Override
  public void setLineThickness(Float thickness) {
    this.lineThickness = thickness == null ? 1.0f : thickness;
  }

  @Override
  public Boolean getSelected() {
    return selected;
  }

  @Override
  public void setSelected(Boolean selected) {
    this.selected = selected != null && selected;
  }

  @Override
  public Boolean getLabelVisible() {
    return labelVisible;
  }

  @Override
  public void setLabelVisible(Boolean visible) {
    this.labelVisible = visible == null || visible;
  }

  @Override
  public String[] getLabel() {
    return label;
  }

  @Override
  public void setLabel(String[] label) {
    this.label = label == null ? new String[0] : label.clone();
  }

  @Override
  public Shape getShape() {
    if (shape == null) {
      buildShape();
    }
    return shape;
  }

  protected void setShape(Shape shape) {
    this.shape = shape;
  }

  @Override
  public Graphic copy() {
    AbstractGraphic copy = newInstance();
    copy.setUuid(UUID.randomUUID().toString());
    copy.setPts(getPts());
    copy.setFilled(getFilled());
    copy.setColorPaint(getColorPaint());
    copy.setLineThickness(getLineThickness());
    copy.setLabelVisible(getLabelVisible());
    copy.setLabel(getLabel());
    copy.fillOpacity = this.fillOpacity;
    copy.lineAlpha = this.lineAlpha;
    return copy;
  }

  public float getFillOpacity() {
    return fillOpacity;
  }

  public void setFillOpacity(float fillOpacity) {
    this.fillOpacity = clamp01(fillOpacity);
  }

  public float getLineAlpha() {
    return lineAlpha;
  }

  public void setLineAlpha(float lineAlpha) {
    this.lineAlpha = clamp01(lineAlpha);
  }

  /** Perceived fill = fillOpacity × lineAlpha (e.g. 0.8 × 0.2 = 0.16). */
  public float perceivedFillAlpha() {
    return fillOpacity * lineAlpha;
  }

  static float clamp01(float v) {
    return Math.max(0f, Math.min(1f, v));
  }

  protected abstract AbstractGraphic newInstance();
}
