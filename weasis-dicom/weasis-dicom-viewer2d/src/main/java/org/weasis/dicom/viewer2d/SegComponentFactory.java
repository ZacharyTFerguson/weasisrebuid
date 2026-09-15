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

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import org.weasis.core.ui.editor.image.ViewCanvas;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegContour;
import org.weasis.core.ui.model.graphic.imp.seg.SegGraphic;
import org.weasis.core.ui.model.graphic.imp.seg.SegRegion;
import org.weasis.dicom.codec.seg.LabelMapScanner;
import org.weasis.dicom.codec.seg.MaskFrames;
import org.weasis.dicom.viewer2d.dockable.SegmentationTool;

/** Builds SEG overlay graphics for a {@link ViewCanvas} from mask frames or contours. */
public class SegComponentFactory {

  public SegGraphic createGraphic(SegContour contour) {
    return new SegGraphic(contour == null ? new SegContour() : contour);
  }

  public SegGraphic createGraphic(SegRegion region, Path2D path) {
    SegContour contour = new SegContour();
    if (region != null) {
      contour.getRegion().setLabel(region.getLabel());
      contour.getRegion().setNumber(region.getNumber());
      contour.getRegion().setColor(region.getColor());
      contour.getRegion().setOpacity(region.getOpacity());
      contour.getRegion().setVisible(region.isVisible());
    }
    if (path != null) {
      contour.setPath(path);
    }
    return new SegGraphic(contour);
  }

  public List<SegGraphic> overlaysForFrame(MaskFrames frames, int frameIndex) {
    List<SegGraphic> graphics = new ArrayList<>();
    if (frames == null || frameIndex < 0 || frameIndex >= frames.size()) {
      return graphics;
    }
    byte[] plane = frames.getFrame(frameIndex);
    for (int label : new LabelMapScanner().scan(plane)) {
      Path2D path = boundingPath(plane, frames.getColumns(), frames.getRows(), label);
      if (path == null) {
        continue;
      }
      SegRegion region = new SegRegion();
      region.setNumber(label);
      region.setLabel("Segment " + label);
      graphics.add(createGraphic(region, path));
    }
    return graphics;
  }

  public List<SegGraphic> applyTo(ViewCanvas view, MaskFrames frames, int frameIndex) {
    List<SegGraphic> graphics = overlaysForFrame(frames, frameIndex);
    if (view == null || !view.isSegmentationsVisible()) {
      return List.of();
    }
    for (SegGraphic graphic : graphics) {
      if (graphic.getContour().getRegion().isVisible()) {
        view.addGraphic(graphic);
      }
    }
    return graphics;
  }

  public void clearOverlays(ViewCanvas view) {
    if (view == null) {
      return;
    }
    List<Graphic> current = new ArrayList<>(view.getGraphicList());
    for (Graphic graphic : current) {
      if (graphic instanceof SegGraphic) {
        view.removeGraphic(graphic);
      }
    }
  }

  public SegmentationTool createTool() {
    return new SegmentationTool();
  }

  static Path2D boundingPath(byte[] plane, int columns, int rows, int label) {
    if (plane == null || columns <= 0 || rows <= 0) {
      return null;
    }
    int minX = columns;
    int minY = rows;
    int maxX = -1;
    int maxY = -1;
    for (int y = 0; y < rows; y++) {
      for (int x = 0; x < columns; x++) {
        int i = y * columns + x;
        if (i < plane.length && (plane[i] & 0xff) == label) {
          minX = Math.min(minX, x);
          minY = Math.min(minY, y);
          maxX = Math.max(maxX, x);
          maxY = Math.max(maxY, y);
        }
      }
    }
    if (maxX < minX) {
      return null;
    }
    Path2D path = new Path2D.Double();
    path.moveTo(minX, minY);
    path.lineTo(maxX + 1, minY);
    path.lineTo(maxX + 1, maxY + 1);
    path.lineTo(minX, maxY + 1);
    path.closePath();
    return path;
  }
}
