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

import java.awt.image.BufferedImage;

/** Pixel under the 3D cursor / pointer (image coordinates and modality value). */
public class PixelInfo {

  private int x;
  private int y;
  private int pixelValue;
  private double modalityValue;
  private String text = "";

  public PixelInfo() {}

  public PixelInfo(int x, int y, int pixelValue, double modalityValue) {
    this.x = x;
    this.y = y;
    this.pixelValue = pixelValue;
    this.modalityValue = modalityValue;
    this.text = x + "," + y + " v=" + pixelValue + " HU=" + modalityValue;
  }

  public static PixelInfo from(BufferedImage image, int x, int y, double slope, double intercept) {
    int value = 0;
    if (image != null && x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) {
      value = image.getRaster().getSample(x, y, 0);
    }
    double m = value * (slope == 0 ? 1.0 : slope) + intercept;
    return new PixelInfo(x, y, value, m);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getPixelValue() {
    return pixelValue;
  }

  public double getModalityValue() {
    return modalityValue;
  }

  public String getText() {
    return text;
  }

  @Override
  public String toString() {
    return text;
  }
}
