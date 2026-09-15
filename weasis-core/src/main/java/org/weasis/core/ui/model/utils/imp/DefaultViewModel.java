/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.api.gui.model.ViewModel;
import org.weasis.core.api.gui.model.ViewModelChangeListener;
import org.weasis.core.api.image.AffineTransformOp;

public class DefaultViewModel implements ViewModel {

  private double modelOffsetX;
  private double modelOffsetY;
  private double viewScale = 1.0;
  private double viewScaleMin = 0.08;
  private double viewScaleMax = 12.0;
  private Rectangle2D modelArea = new Rectangle2D.Double(0, 0, 512, 512);
  private final List<ViewModelChangeListener> listeners = new CopyOnWriteArrayList<>();

  @Override
  public double getModelOffsetX() {
    return modelOffsetX;
  }

  @Override
  public double getModelOffsetY() {
    return modelOffsetY;
  }

  @Override
  public void setModelOffset(double x, double y) {
    this.modelOffsetX = x;
    this.modelOffsetY = y;
    fire();
  }

  @Override
  public double getViewScale() {
    return viewScale;
  }

  @Override
  public void setViewScale(double viewScale) {
    if (viewScale == AffineTransformOp.ZOOM_BEST_FIT
        || viewScale == AffineTransformOp.ZOOM_REAL_SIZE) {
      this.viewScale = viewScale;
    } else {
      this.viewScale = Math.max(viewScaleMin, Math.min(viewScaleMax, viewScale));
    }
    fire();
  }

  @Override
  public double getViewScaleMin() {
    return viewScaleMin;
  }

  @Override
  public double getViewScaleMax() {
    return viewScaleMax;
  }

  @Override
  public void setViewScaleMinMax(double min, double max) {
    this.viewScaleMin = min;
    this.viewScaleMax = max;
  }

  @Override
  public Rectangle2D getModelArea() {
    return modelArea;
  }

  @Override
  public void setModelArea(Rectangle2D area) {
    this.modelArea = area == null ? new Rectangle2D.Double() : area;
    fire();
  }

  @Override
  public void addViewModelChangeListener(ViewModelChangeListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  @Override
  public void removeViewModelChangeListener(ViewModelChangeListener listener) {
    listeners.remove(listener);
  }

  private void fire() {
    for (ViewModelChangeListener l : listeners) {
      l.handleViewModelChanged(this);
    }
  }
}
