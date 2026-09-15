/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer;

/** Named canvas layer (image, measure, draw, annotations, …). */
public class Layer {

  private LayerType type = LayerType.IMAGE;
  private boolean visible = true;
  private int level;

  public Layer() {}

  public Layer(LayerType type) {
    setType(type);
  }

  public LayerType getType() {
    return type;
  }

  public void setType(LayerType type) {
    this.type = type == null ? LayerType.IMAGE : type;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
