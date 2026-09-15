/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

public class FboRenderTexture {

  private int width;
  private int height;
  private boolean bound;

  public void resize(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public void bind() {
    bound = true;
  }

  public void unbind() {
    bound = false;
  }

  public boolean isBound() {
    return bound;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
