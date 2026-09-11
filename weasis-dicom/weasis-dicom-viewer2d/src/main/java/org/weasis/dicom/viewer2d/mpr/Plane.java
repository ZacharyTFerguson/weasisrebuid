/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/** Orthogonal plane plus oblique (gantry / user rotation). */
public enum Plane {
  AXIAL("blue"),
  CORONAL("red"),
  SAGITTAL("green"),
  OBLIQUE("white");

  private final String perpendicularColor;

  Plane(String perpendicularColor) {
    this.perpendicularColor = perpendicularColor;
  }

  /** Small square color: blue L/R, red A/P, green F/H. */
  public String perpendicularColor() {
    return perpendicularColor;
  }
}
