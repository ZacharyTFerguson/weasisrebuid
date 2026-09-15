/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions.rectify;

import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.operations.impl.RectifyOrientationChangeListener;
import org.weasis.acquire.operations.impl.RotationActionListener;

/** Orientation slider and 90° buttons that write pending {@link AcquireImageValues}. */
public class RectifyPanel {

  private final OrientationSliderComponent orientation = new OrientationSliderComponent();
  private final Rotate90Button rotate90 = new Rotate90Button();
  private final Rotate270Button rotate270 = new Rotate270Button();

  public OrientationSliderComponent orientation() {
    return orientation;
  }

  public Rotate90Button rotate90() {
    return rotate90;
  }

  public Rotate270Button rotate270() {
    return rotate270;
  }

  public void applyOrientation(AcquireImageValues values) {
    applyOrientation(values, null);
  }

  public void applyOrientation(
      AcquireImageValues values, RectifyOrientationChangeListener listener) {
    orientation.applyTo(values, listener);
  }

  public void rotate90(AcquireImageValues values) {
    rotate90(values, null);
  }

  public void rotate90(AcquireImageValues values, RotationActionListener listener) {
    rotate90.apply(values, listener);
  }

  public void rotate270(AcquireImageValues values) {
    rotate270(values, null);
  }

  public void rotate270(AcquireImageValues values, RotationActionListener listener) {
    rotate270.apply(values, listener);
  }
}
