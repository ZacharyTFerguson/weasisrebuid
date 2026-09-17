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

/** Piecewise polynomials between knots (DVH bin interpolation). */
public class PolynomialSplineFunction {

  private final double[] knots;
  private final PolynomialFunction[] pieces;

  public PolynomialSplineFunction(double[] knots, PolynomialFunction[] pieces) {
    this.knots = knots == null ? new double[0] : knots.clone();
    this.pieces = pieces == null ? new PolynomialFunction[0] : pieces.clone();
  }

  public double value(double x) {
    if (pieces.length == 0) {
      return 0;
    }
    int i = 0;
    while (i + 1 < knots.length && x >= knots[i + 1]) {
      i++;
    }
    if (i >= pieces.length) {
      i = pieces.length - 1;
    }
    double local = x - (i < knots.length ? knots[i] : 0);
    return pieces[i].value(local);
  }
}
