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

import org.weasis.core.api.image.AbstractOp;

/** Per-pane fusion op. MPR panes each hold their own instance (MX-04). */
public final class FusionOp extends AbstractOp {

  public static final String LUT = "fusion.lut";
  public static final String SERIES = "fusion.series";
  public static final String OPACITY = "fusion.opacity";

  public FusionOp() {
    super("op.fusion");
    setParam(OPACITY, 1.0);
  }

  public FusionOp copy() {
    FusionOp copy = new FusionOp();
    copy.setEnabled(isEnabled());
    copy.setParam(LUT, getParam(LUT));
    copy.setParam(SERIES, getParam(SERIES));
    copy.setParam(OPACITY, getParam(OPACITY));
    return copy;
  }

  public double opacity() {
    Object v = getParam(OPACITY, 1.0);
    return v instanceof Number n ? n.doubleValue() : 1.0;
  }
}
