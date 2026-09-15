/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/** Geometric image canvas: source pixels plus zoom, pan, rotation, and view↔image mapping. */
public interface Canvas {

  BufferedImage getSourceImage();

  void setSourceImage(BufferedImage source);

  double getZoom();

  void setZoom(double zoom);

  double getPanX();

  double getPanY();

  void setPan(double x, double y);

  double getRotation();

  void setRotation(double rotation);

  Point2D.Double viewToImage(double viewX, double viewY);

  Point2D.Double imageToView(double imageX, double imageY);

  AffineTransform getAffineTransform();
}
