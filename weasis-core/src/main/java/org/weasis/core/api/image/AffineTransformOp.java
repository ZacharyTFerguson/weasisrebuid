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

/** Last View2d node: zoom / pan / rotation applied at paint time. */
public class AffineTransformOp extends AbstractOp {

  public static final String P_ZOOM = "zoom";
  public static final String P_ROTATION = "rotation";
  public static final String P_PAN_X = "pan.x";
  public static final String P_PAN_Y = "pan.y";

  /** Magic zoom: best fit (default). */
  public static final double ZOOM_BEST_FIT = -200.0;

  /** Magic zoom: real-world / actual pixels (1:1). */
  public static final double ZOOM_REAL_SIZE = -100.0;

  public AffineTransformOp() {
    super("op.affine");
    setParam(P_ZOOM, ZOOM_BEST_FIT);
    setParam(P_ROTATION, 0.0);
    setParam(P_PAN_X, 0.0);
    setParam(P_PAN_Y, 0.0);
  }
}
