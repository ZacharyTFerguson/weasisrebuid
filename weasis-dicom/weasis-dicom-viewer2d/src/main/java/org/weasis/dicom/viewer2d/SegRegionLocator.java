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

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.core.ui.editor.image.ViewCanvas;
import org.weasis.core.ui.model.graphic.imp.seg.SegGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegRegion;
import org.weasis.dicom.codec.seg.MaskFrames;

/**
 * Resolves which SEG region occupies an image coordinate (click on overlay). Mask pixels use the
 * stored label; overlay graphics use {@link Shape#contains}.
 */
public class SegRegionLocator {

  public int labelAt(MaskFrames frames, int frameIndex, int x, int y) {
    if (frames == null || frameIndex < 0 || frameIndex >= frames.size() || x < 0 || y < 0) {
      return 0;
    }
    int columns = frames.getColumns();
    int rows = frames.getRows();
    if (x >= columns || y >= rows) {
      return 0;
    }
    byte[] plane = frames.getFrame(frameIndex);
    int i = y * columns + x;
    if (i < 0 || i >= plane.length) {
      return 0;
    }
    return plane[i] & 0xff;
  }

  public SegRegion locate(MaskFrames frames, int frameIndex, int x, int y) {
    return locate(frames, frameIndex, x, y, List.of());
  }

  public SegRegion locate(
      MaskFrames frames, int frameIndex, int x, int y, List<SegRegion> catalog) {
    int label = labelAt(frames, frameIndex, x, y);
    if (label == 0) {
      return null;
    }
    if (catalog != null) {
      for (SegRegion region : catalog) {
        if (region != null && region.getNumber() == label) {
          return region.isVisible() ? region : null;
        }
      }
    }
    SegRegion created = new SegRegion();
    created.setNumber(label);
    created.setLabel("Segment " + label);
    return created;
  }

  public SegGraphic locate(List<SegGraphic> overlays, double x, double y) {
    if (overlays == null) {
      return null;
    }
    Point2D.Double pt = new Point2D.Double(x, y);
    for (SegGraphic graphic : overlays) {
      if (graphic == null || graphic.getContour() == null) {
        continue;
      }
      SegRegion region = graphic.getContour().getRegion();
      if (region != null && !region.isVisible()) {
        continue;
      }
      Shape shape = graphic.getShape();
      if (shape != null && shape.contains(pt)) {
        return graphic;
      }
    }
    return null;
  }

  public SegGraphic locate(ViewCanvas view, double x, double y) {
    if (view == null || !view.isSegmentationsVisible()) {
      return null;
    }
    return locate(segGraphics(view), x, y);
  }

  static List<SegGraphic> segGraphics(ViewCanvas view) {
    return view.getGraphicList().stream()
        .filter(SegGraphic.class::isInstance)
        .map(SegGraphic.class::cast)
        .toList();
  }

  public SegRegion locateRegion(ViewCanvas view, double x, double y) {
    SegGraphic graphic = locate(view, x, y);
    if (graphic == null || graphic.getContour() == null) {
      return null;
    }
    return graphic.getContour().getRegion();
  }
}
