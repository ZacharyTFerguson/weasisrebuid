/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer.imp;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.api.image.measure.MeasurementsAdapter;
import org.weasis.core.api.image.util.MeasurableLayer;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.core.ui.model.utils.ImageLayerChangeListener;

/** IMAGE layer that holds the current raster and notifies listeners when it changes. */
public class RenderedImageLayer extends DefaultLayer implements MeasurableLayer {

  private final List<ImageLayerChangeListener> listeners = new CopyOnWriteArrayList<>();
  private BufferedImage image;

  public RenderedImageLayer() {
    super(LayerType.IMAGE);
  }

  public BufferedImage getImage() {
    return image;
  }

  public void setImage(BufferedImage image) {
    this.image = image;
    fire();
  }

  public void addLayerChangeListener(ImageLayerChangeListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  public void removeLayerChangeListener(ImageLayerChangeListener listener) {
    listeners.remove(listener);
  }

  void fire() {
    for (ImageLayerChangeListener listener : listeners) {
      listener.handleLayerChanged();
    }
  }

  @Override
  public MeasurementsAdapter getMeasurementAdapter(Unit displayUnit) {
    return new MeasurementsAdapter(1.0, displayUnit == null ? Unit.PIXEL : displayUnit);
  }

  @Override
  public AffineTransform getAffineTransform() {
    return new AffineTransform();
  }

  @Override
  public boolean hasContent() {
    return image != null;
  }
}
