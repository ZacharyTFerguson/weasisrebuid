/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.media.data;

import java.awt.image.BufferedImage;
import java.net.URI;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.api.image.util.WindLevelParameters;

public class ImageElement extends MediaElement {
  private final FileCache fileCache = new FileCache(this);
  private double pixelSizeX = 1.0;
  private double pixelSizeY = 1.0;
  private Unit pixelSpacingUnit = Unit.PIXEL;
  private double window = 255;
  private double level = 127.5;
  private boolean invertedLut;
  private BufferedImage image;

  public ImageElement() {}

  public ImageElement(URI uri) {
    setMediaURI(uri);
  }

  public FileCache getFileCache() {
    return fileCache;
  }

  public double getPixelSize() {
    return pixelSizeX;
  }

  public double getPixelSizeX() {
    return pixelSizeX;
  }

  public double getPixelSizeY() {
    return pixelSizeY;
  }

  public void setPixelSize(double x, double y) {
    this.pixelSizeX = x <= 0 ? 1.0 : x;
    this.pixelSizeY = y <= 0 ? 1.0 : y;
  }

  public Unit getPixelSpacingUnit() {
    return pixelSpacingUnit;
  }

  public void setPixelSpacingUnit(Unit unit) {
    this.pixelSpacingUnit = unit == null ? Unit.PIXEL : unit;
  }

  public WindLevelParameters getWindLevelParameters() {
    return new WindLevelParameters(window, level);
  }

  public void setWindowLevel(double window, double level) {
    this.window = window;
    this.level = level;
  }

  public boolean isInvertedLut() {
    return invertedLut;
  }

  public void setInvertedLut(boolean invertedLut) {
    this.invertedLut = invertedLut;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }
}
