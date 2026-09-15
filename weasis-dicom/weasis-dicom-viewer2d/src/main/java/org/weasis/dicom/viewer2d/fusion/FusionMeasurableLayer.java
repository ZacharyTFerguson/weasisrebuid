/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import java.awt.geom.AffineTransform;
import org.weasis.core.api.image.measure.MeasurementsAdapter;
import org.weasis.core.api.image.util.MeasurableLayer;
import org.weasis.core.api.image.util.Unit;

/**
 * Measurement layer for a fused overlay: pixel spacing on the overlay FoR when the stack has
 * content.
 */
public class FusionMeasurableLayer implements MeasurableLayer {

  private FusionStack stack = FusionStack.empty();
  private double pixelSpacingMm = 1.0;
  private AffineTransform transform = new AffineTransform();

  public FusionMeasurableLayer() {}

  public FusionMeasurableLayer(FusionStack stack, double pixelSpacingMm) {
    bind(stack, pixelSpacingMm);
  }

  public void bind(FusionStack stack, double pixelSpacingMm) {
    this.stack = stack == null ? FusionStack.empty() : stack;
    this.pixelSpacingMm = pixelSpacingMm <= 0 ? 1.0 : pixelSpacingMm;
  }

  public FusionStack stack() {
    return stack;
  }

  public double pixelSpacingMm() {
    return pixelSpacingMm;
  }

  public void setAffineTransform(AffineTransform transform) {
    this.transform = transform == null ? new AffineTransform() : new AffineTransform(transform);
  }

  @Override
  public MeasurementsAdapter getMeasurementAdapter(Unit displayUnit) {
    Unit unit = displayUnit == null ? Unit.MILLIMETER : displayUnit;
    double ratio = unit == Unit.PIXEL ? 1.0 : pixelSpacingMm / unit.getConvMm();
    return new MeasurementsAdapter(ratio, unit);
  }

  @Override
  public AffineTransform getAffineTransform() {
    return new AffineTransform(transform);
  }

  @Override
  public boolean hasContent() {
    return stack != null && !stack.isEmpty();
  }
}
