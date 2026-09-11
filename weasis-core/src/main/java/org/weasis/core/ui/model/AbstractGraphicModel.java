/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.ui.model.graphic.Graphic;

public class AbstractGraphicModel implements GraphicModel {

  private final List<Graphic> models = new CopyOnWriteArrayList<>();

  @Override
  public List<Graphic> getModels() {
    return Collections.unmodifiableList(models);
  }

  @Override
  public void addGraphic(Graphic graphic) {
    if (graphic != null && !models.contains(graphic)) {
      models.add(graphic);
    }
  }

  @Override
  public void removeGraphic(Graphic graphic) {
    models.remove(graphic);
  }

  @Override
  public void clear() {
    models.clear();
  }

  @Override
  public List<Graphic> getSelectedGraphics() {
    List<Graphic> selected = new ArrayList<>();
    for (Graphic g : models) {
      if (Boolean.TRUE.equals(g.getSelected())) {
        selected.add(g);
      }
    }
    return selected;
  }
}
