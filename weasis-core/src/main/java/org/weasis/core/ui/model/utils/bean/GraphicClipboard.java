/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

import java.util.ArrayList;
import java.util.List;
import org.weasis.core.ui.model.graphic.Graphic;

/** Holds copied graphics so a model can paste independent copies. */
public class GraphicClipboard {

  private List<Graphic> graphics = List.of();

  public void setGraphics(List<Graphic> graphics) {
    this.graphics = copies(graphics);
  }

  public List<Graphic> getGraphics() {
    return graphics;
  }

  public boolean hasGraphics() {
    return !graphics.isEmpty();
  }

  public void clear() {
    graphics = List.of();
  }

  static List<Graphic> copies(List<Graphic> graphics) {
    if (graphics == null || graphics.isEmpty()) {
      return List.of();
    }
    List<Graphic> out = new ArrayList<>();
    for (Graphic graphic : graphics) {
      addCopy(out, graphic);
    }
    return List.copyOf(out);
  }

  static void addCopy(List<Graphic> out, Graphic graphic) {
    if (graphic != null) {
      out.add(graphic.copy());
    }
  }
}
