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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.weasis.core.ui.model.utils.imp.DefaultUUID;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractGraphic extends DefaultUUID implements Graphic {

  private final List<Point2D.Double> pts = new ArrayList<>();
  private final List<XmlPt> xmlPts = new ArrayList<>();
  private Boolean filled = Boolean.FALSE;
  private Paint colorPaint = Color.YELLOW;
  private Float lineThickness = 1.0f;
  private Boolean selected = Boolean.FALSE;
  private Boolean labelVisible = Boolean.TRUE;
  private String[] label = new String[0];
  private Shape shape;

  protected AbstractGraphic(int expectedPts) {
    for (int i = 0; i < expectedPts; i++) {
      pts.add(new Point2D.Double(0, 0));
    }
  }

  @XmlAttribute
  @Override
  public String getUuid() {
    return super.getUuid();
  }

  @Override
  public void setUuid(String uuid) {
    super.setUuid(uuid);
  }

  @Override
  public Integer getPtsNumber() {
    return pts.size();
  }

  @XmlElement(name = "pt")
  public List<XmlPt> getXmlPts() {
    return xmlPts;
  }

  public void afterUnmarshal(jakarta.xml.bind.Unmarshaller unmarshaller, Object parent) {
    pts.clear();
    for (XmlPt pt : xmlPts) {
      pts.add(new Point2D.Double(pt.x, pt.y));
    }
    buildShape();
  }

  @XmlTransient
  @Override
  public List<Point2D.Double> getPts() {
    return Collections.unmodifiableList(pts);
  }

  @Override
  public void setPts(List<Point2D.Double> newPts) {
    pts.clear();
    xmlPts.clear();
    if (newPts != null) {
      for (Point2D.Double p : newPts) {
        Point2D.Double copy =
            p == null ? new Point2D.Double() : new Point2D.Double(p.getX(), p.getY());
        pts.add(copy);
        xmlPts.add(new XmlPt(copy.getX(), copy.getY()));
      }
    }
    buildShape();
  }

  protected List<Point2D.Double> ptsMutable() {
    return pts;
  }

  @XmlAttribute
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

  @XmlAttribute
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

  @XmlAttribute
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
    return copy;
  }

  protected abstract AbstractGraphic newInstance();

  @XmlAccessorType(XmlAccessType.FIELD)
  public static final class XmlPt {
    @XmlAttribute public double x;
    @XmlAttribute public double y;

    public XmlPt() {}

    public XmlPt(double x, double y) {
      this.x = x;
      this.y = y;
    }
  }
}
