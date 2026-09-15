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

/** Linear blend of base (CT) and overlay (PET) samples. */
public class FusionOp {

  public double blend(double base, double overlay, double opacity) {
    double a = Math.max(0, Math.min(1, opacity));
    return base * (1.0 - a) + overlay * a;
  }
}
