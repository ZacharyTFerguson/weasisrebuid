/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.PointGraphic;
import org.weasis.core.ui.model.graphic.imp.area.EllipseGraphic;
import org.weasis.core.ui.model.graphic.imp.line.LineGraphic;
import org.weasis.core.ui.model.graphic.imp.line.PolylineGraphic;

/** Load GSPS graphics (drawings). Does not apply W/L or zoom from a complete PR. */
public final class PresentationStateReader {

  private PresentationStateReader() {}

  public static List<Graphic> readFile(File file) throws Exception {
    try (DicomInputStream in = new DicomInputStream(file)) {
      in.setIncludeBulkData(DicomInputStream.IncludeBulkData.NO);
      return read(in.readDataset());
    }
  }

  public static List<Graphic> read(Attributes pr) {
    List<Graphic> out = new ArrayList<>();
    if (pr == null) {
      return out;
    }
    Sequence anns = pr.getSequence(Tag.GraphicAnnotationSequence);
    if (anns == null) {
      return out;
    }
    for (Attributes ann : anns) {
      Sequence objs = ann.getSequence(Tag.GraphicObjectSequence);
      if (objs == null) {
        continue;
      }
      for (Attributes obj : objs) {
        Graphic g = fromObject(obj);
        if (g != null) {
          out.add(g);
        }
      }
    }
    return out;
  }

  static Graphic fromObject(Attributes obj) {
    float[] data = obj.getFloats(Tag.GraphicData);
    if (data == null || data.length < 2) {
      return null;
    }
    List<Point2D.Double> pts = new ArrayList<>();
    for (int i = 0; i + 1 < data.length; i += 2) {
      pts.add(new Point2D.Double(data[i], data[i + 1]));
    }
    String type = obj.getString(Tag.GraphicType, "POLYLINE");
    Graphic g;
    if ("ELLIPSE".equals(type) && pts.size() >= 2) {
      g = new EllipseGraphic();
    } else if ("POINT".equals(type) || pts.size() == 1) {
      g = new PointGraphic();
    } else if (pts.size() == 2) {
      g = new LineGraphic();
    } else {
      g = new PolylineGraphic();
    }
    g.setPts(pts);
    g.setFilled("Y".equals(obj.getString(Tag.GraphicFilled, "N")));
    return g;
  }
}
