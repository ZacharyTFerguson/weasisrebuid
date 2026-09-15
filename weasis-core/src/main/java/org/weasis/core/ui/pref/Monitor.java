/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;

/** Monitor spatial calibration (MX-07). Independent from session Manual Calibration. */
public class Monitor {
  private final GraphicsDevice device;
  private double pitchXmm = 0.25;

  public Monitor(GraphicsDevice device) {
    this.device = device;
  }

  public GraphicsDevice getGraphicsDevice() {
    return device;
  }

  public GraphicsConfiguration getGraphicsConfiguration() {
    return device == null ? null : device.getDefaultConfiguration();
  }

  public double getPitchXmm() {
    return pitchXmm;
  }

  public void setPitchXmm(double pitchXmm) {
    this.pitchXmm = pitchXmm;
  }
}
