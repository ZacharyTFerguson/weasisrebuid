/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.dockable;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.AnnotationGraphic;
import org.weasis.core.ui.model.graphic.imp.angle.AngleToolGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.area.PolygonGraphic;
import org.weasis.core.ui.model.graphic.imp.area.RectangleGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/** Dockable measure tool. Maps documented 2D shortcuts (M/D/A/Y/G/B) onto graphic constructors. */
public final class MeasureTool {

  public static final String DISTANCE = "distance";
  public static final String ANGLE = "angle";
  public static final String POLYLINE = "polyline";
  public static final String TEXTBOX = "textbox";
  public static final String RECTANGLE = "rectangle";
  public static final String ELLIPSE = "ellipse";
  public static final String POLYGON = "polygon";

  public static final List<String> NAMES =
      List.of(DISTANCE, ANGLE, POLYLINE, TEXTBOX, RECTANGLE, ELLIPSE, POLYGON);

  private static final Map<String, Supplier<Graphic>> TOOLS = new LinkedHashMap<>();

  static {
    TOOLS.put(DISTANCE, LineGraphic::new);
    TOOLS.put(ANGLE, AngleToolGraphic::new);
    TOOLS.put(POLYLINE, PolylineGraphic::new);
    TOOLS.put(TEXTBOX, AnnotationGraphic::new);
    TOOLS.put(RECTANGLE, RectangleGraphic::new);
    TOOLS.put(ELLIPSE, EllipseGraphic::new);
    TOOLS.put(POLYGON, PolygonGraphic::new);
    TOOLS.put("D", LineGraphic::new);
    TOOLS.put("A", AngleToolGraphic::new);
    TOOLS.put("Y", PolylineGraphic::new);
    TOOLS.put("B", AnnotationGraphic::new);
    TOOLS.put("G", RectangleGraphic::new);
    TOOLS.put("M", LineGraphic::new);
  }

  private MeasureTool() {}

  public static Graphic create(String tool) {
    if (tool == null) {
      return null;
    }
    Supplier<Graphic> s = TOOLS.get(canonical(tool));
    if (s == null) {
      s = TOOLS.get(tool);
    }
    if (s == null) {
      s = TOOLS.get(tool.toLowerCase());
    }
    Graphic graphic = s == null ? null : s.get();
    if (graphic != null) {
      graphic.setLineThickness(3.0f);
    }
    return graphic;
  }

  public static String canonical(String tool) {
    if (tool == null || tool.isBlank()) {
      return DISTANCE;
    }
    return switch (tool) {
      case "A", "angle" -> ANGLE;
      case "Y", "polyline" -> POLYLINE;
      case "G", "rectangle", "draw" -> RECTANGLE;
      case "B", "textbox" -> TEXTBOX;
      case "M", "D", "distance", "measure" -> DISTANCE;
      default -> tool.toLowerCase();
    };
  }

  public static boolean measureFamily(String tool) {
    String c = canonical(tool);
    return DISTANCE.equals(c) || ANGLE.equals(c) || POLYLINE.equals(c) || TEXTBOX.equals(c);
  }

  public static boolean drawFamily(String tool) {
    String c = canonical(tool);
    return RECTANGLE.equals(c) || ELLIPSE.equals(c) || POLYGON.equals(c);
  }

  public static Graphic distance(Point2D.Double a, Point2D.Double b) {
    LineGraphic line = new LineGraphic();
    line.setHandlePoint(0, a);
    line.setHandlePoint(1, b);
    return line;
  }
}
