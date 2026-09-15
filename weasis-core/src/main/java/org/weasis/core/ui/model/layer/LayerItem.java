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

/** Named Display-dock item (image, crosslines, annotations, drawings, measurements). */
public class LayerItem {

  private LayerType type = LayerType.IMAGE;
  private String name = LayerType.IMAGE.name();
  private boolean selected = true;

  public LayerItem() {}

  public LayerItem(LayerType type) {
    setType(type);
  }

  public LayerType getType() {
    return type;
  }

  public void setType(LayerType type) {
    this.type = type == null ? LayerType.IMAGE : type;
    if (name == null || name.isBlank() || name.equals(LayerType.IMAGE.name())) {
      this.name = this.type.name();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name == null || name.isBlank() ? type.name() : name;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }
}
