/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

public class PrintOptions {
  private boolean showingAnnotations = true;
  private double imageScale = 1.0;

  public boolean isShowingAnnotations() {
    return showingAnnotations;
  }

  public void setShowingAnnotations(boolean showingAnnotations) {
    this.showingAnnotations = showingAnnotations;
  }

  public double getImageScale() {
    return imageScale;
  }

  public void setImageScale(double imageScale) {
    this.imageScale = imageScale;
  }
}
