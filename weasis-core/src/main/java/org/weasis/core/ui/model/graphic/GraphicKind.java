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

import java.util.function.Supplier;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.NonEditableGraphic;
import org.weasis.core.ui.model.graphic.imp.PixelInfoGraphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.CobbAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.FourPointsAngleGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.OpenAngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ObliqueRectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.area.SelectGraphic;
import org.weasis.core.ui.model.graphic.imp.area.ThreePointsCircleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineWithGapGraphic;
import org.weasis.core.ui.model.graphic.imp.line.ParallelLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PerpendicularLineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegContour;

/** CHECKLIST §4.4 / ARCHITECTURE §5.2 graphic catalog. */
public enum GraphicKind {
  LINE("Line", LineGraphic::new, true),
  LINE_WITH_GAP("LineWithGap", LineWithGapGraphic::new, true),
  PARALLEL("Parallel", ParallelLineGraphic::new, true),
  PERPENDICULAR("Perpendicular", PerpendicularLineGraphic::new, true),
  POLYLINE("Polyline", PolylineGraphic::new, true),
  ELLIPSE("Ellipse", EllipseGraphic::new, true),
  RECTANGLE("Rectangle", RectangleGraphic::new, true),
  OBLIQUE_RECTANGLE("ObliqueRectangle", ObliqueRectangleGraphic::new, true),
  POLYGON("Polygon", PolygonGraphic::new, true),
  THREE_POINTS_CIRCLE("ThreePointsCircle", ThreePointsCircleGraphic::new, true),
  SELECT("Select", SelectGraphic::new, false),
  ANGLE("Angle", AngleToolGraphic::new, true),
  OPEN_ANGLE("OpenAngle", OpenAngleToolGraphic::new, true),
  COBB("Cobb", CobbAngleToolGraphic::new, true),
  FOUR_POINTS_ANGLE("FourPointsAngle", FourPointsAngleGraphic::new, true),
  POINT("Point", PointGraphic::new, true),
  ANNOTATION("Annotation", AnnotationGraphic::new, false),
  PIXEL_INFO("PixelInfo", PixelInfoGraphic::new, true),
  NON_EDITABLE("NonEditable", () -> new NonEditableGraphic(null), false),
  SEG_CONTOUR("SegContour", SegContour::new, false);

  private final String xmlName;
  private final Supplier<Graphic> factory;
  private final boolean measurement;

  GraphicKind(String xmlName, Supplier<Graphic> factory, boolean measurement) {
    this.xmlName = xmlName;
    this.factory = factory;
    this.measurement = measurement;
  }

  public String xmlName() {
    return xmlName;
  }

  public Graphic create() {
    return factory.get();
  }

  public boolean measurement() {
    return measurement;
  }

  public static GraphicKind fromXml(String name) {
    if (name == null) {
      return null;
    }
    for (GraphicKind k : values()) {
      if (k.xmlName.equalsIgnoreCase(name) || k.name().equalsIgnoreCase(name)) {
        return k;
      }
    }
    return null;
  }

  public static GraphicKind of(Graphic g) {
    if (g == null) {
      return null;
    }
    Class<?> c = g.getClass();
    if (c == LineWithGapGraphic.class) {
      return LINE_WITH_GAP;
    }
    if (c == LineGraphic.class) {
      return LINE;
    }
    if (c == ParallelLineGraphic.class) {
      return PARALLEL;
    }
    if (c == PerpendicularLineGraphic.class) {
      return PERPENDICULAR;
    }
    if (c == PolylineGraphic.class) {
      return POLYLINE;
    }
    if (c == EllipseGraphic.class) {
      return ELLIPSE;
    }
    if (c == SelectGraphic.class) {
      return SELECT;
    }
    if (c == ObliqueRectangleGraphic.class) {
      return OBLIQUE_RECTANGLE;
    }
    if (c == RectangleGraphic.class) {
      return RECTANGLE;
    }
    if (c == PolygonGraphic.class) {
      return POLYGON;
    }
    if (c == ThreePointsCircleGraphic.class) {
      return THREE_POINTS_CIRCLE;
    }
    if (c == AngleToolGraphic.class) {
      return ANGLE;
    }
    if (c == OpenAngleToolGraphic.class) {
      return OPEN_ANGLE;
    }
    if (c == CobbAngleToolGraphic.class) {
      return COBB;
    }
    if (c == FourPointsAngleGraphic.class) {
      return FOUR_POINTS_ANGLE;
    }
    if (c == PointGraphic.class) {
      return POINT;
    }
    if (c == AnnotationGraphic.class) {
      return ANNOTATION;
    }
    if (c == PixelInfoGraphic.class) {
      return PIXEL_INFO;
    }
    if (c == SegContour.class) {
      return SEG_CONTOUR;
    }
    if (g instanceof NonEditableGraphic) {
      return NON_EDITABLE;
    }
    return null;
  }
}
