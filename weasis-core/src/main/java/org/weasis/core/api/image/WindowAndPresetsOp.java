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

/** View2d VOI node: same linear/SIGMOID window as {@link WindowOp}. */
public class WindowAndPresetsOp extends AbstractOp {

  public static final String P_WINDOW = "window";
  public static final String P_LEVEL = "level";
  public static final String P_VOI_LUT_SHAPE = "voi.shape";
  public static final String P_PRESET = "preset";
  public static final String P_INVERT = "invert";

  public WindowAndPresetsOp() {
    super("op.window.presets");
  }

  @Override
  protected void processEnabled() {
    WindowOp window = new WindowOp();
    window.setParam(WindowOp.P_WINDOW, getParam(P_WINDOW));
    window.setParam(WindowOp.P_LEVEL, getParam(P_LEVEL));
    window.setParam(WindowOp.P_VOI_LUT_SHAPE, getParam(P_VOI_LUT_SHAPE));
    window.setParam(INPUT_IMG, getParam(INPUT_IMG));
    window.processEnabled();
    setParam(OUTPUT_IMG, window.getParam(OUTPUT_IMG));
  }
}
