/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.model;

import java.awt.geom.Rectangle2D;

public interface ViewModel {
  double getModelOffsetX();

  double getModelOffsetY();

  void setModelOffset(double x, double y);

  double getViewScale();

  void setViewScale(double viewScale);

  double getViewScaleMin();

  double getViewScaleMax();

  void setViewScaleMinMax(double min, double max);

  Rectangle2D getModelArea();

  void setModelArea(Rectangle2D area);

  void addViewModelChangeListener(ViewModelChangeListener listener);

  void removeViewModelChangeListener(ViewModelChangeListener listener);
}
