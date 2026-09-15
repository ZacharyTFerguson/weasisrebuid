/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire;

import java.awt.image.BufferedImage;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireImageValues;

/** Bound photo-editor session: current image, info, and pending values. */
public class AcquireObject {

  private AcquireImageInfo imageInfo;
  private AcquireImageValues values = new AcquireImageValues();
  private BufferedImage image;

  public AcquireImageInfo getImageInfo() {
    return imageInfo;
  }

  public void setImageInfo(AcquireImageInfo imageInfo) {
    this.imageInfo = imageInfo;
  }

  public AcquireImageValues getImageValues() {
    return values;
  }

  public void setImageValues(AcquireImageValues values) {
    this.values = values == null ? new AcquireImageValues() : values;
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
  }
}
