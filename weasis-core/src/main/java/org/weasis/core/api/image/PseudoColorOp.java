/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

/** Pseudo-color LUT after filter. Gray is identity. */
public class PseudoColorOp extends AbstractOp {

  public static final String P_LUT = "lut";
  public static final String P_INVERT = "invert";
  public static final String GRAY = "Gray";

  public PseudoColorOp() {
    super("op.pseudocolor");
    setParam(P_LUT, GRAY);
    setParam(P_INVERT, Boolean.FALSE);
  }
}
