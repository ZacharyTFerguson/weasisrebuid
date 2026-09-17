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

/** Layer that holds measure/draw graphics. Defaults to {@link LayerType#MEASURE}. */
public class GraphicLayer extends Layer {

  public GraphicLayer() {
    this(LayerType.MEASURE);
  }

  public GraphicLayer(LayerType type) {
    super(type == null ? LayerType.MEASURE : type);
  }
}
