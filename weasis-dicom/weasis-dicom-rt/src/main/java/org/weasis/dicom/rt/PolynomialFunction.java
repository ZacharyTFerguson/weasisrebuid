/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

/** Polynomial a0 + a1 x + a2 x^2 + … */
public class PolynomialFunction {

  private final double[] coefficients;

  public PolynomialFunction(double[] coefficients) {
    this.coefficients = coefficients == null ? new double[] {0} : coefficients.clone();
  }

  public double value(double x) {
    double acc = 0;
    double pow = 1;
    for (double c : coefficients) {
      acc += c * pow;
      pow *= x;
    }
    return acc;
  }
}
