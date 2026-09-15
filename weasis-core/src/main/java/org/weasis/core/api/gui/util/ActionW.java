/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

import java.awt.event.KeyEvent;

/**
 * Viewer action identifiers. Gogo {@code dcmview2d:mouseLeftAction} tokens match {@link #cmd()}.
 */
public class ActionW extends Feature<Object> {

  public static final ActionW WINDOW = new ActionW("window", 0.0);
  public static final ActionW LEVEL = new ActionW("level", 0.0);
  public static final ActionW WINLEVEL = new ActionW("winLevel", KeyEvent.VK_W, null);
  public static final ActionW SCROLL_SERIES = new ActionW("sequence", KeyEvent.VK_S, 0);
  public static final ActionW ZOOM = new ActionW("zoom", KeyEvent.VK_Z, 0.0);
  public static final ActionW ROTATION = new ActionW("rotation", KeyEvent.VK_R, 0);
  public static final ActionW PAN = new ActionW("pan", KeyEvent.VK_T, null);
  public static final ActionW CROSSHAIR = new ActionW("crosshair", KeyEvent.VK_H, null);
  public static final ActionW MEASURE = new ActionW("measure", KeyEvent.VK_M, null);
  public static final ActionW DRAW = new ActionW("draw", KeyEvent.VK_G, null);
  public static final ActionW CONTEXTMENU = new ActionW("contextMenu", KeyEvent.VK_Q, null);
  public static final ActionW NONE = new ActionW("none", KeyEvent.VK_N, null);
  public static final ActionW SYNCH = new ActionW("synch", "Stack");
  public static final ActionW LAYOUT = new ActionW("layout", 1);
  public static final ActionW RESET = new ActionW("reset", null);
  public static final ActionW CINE = new ActionW("cine", KeyEvent.VK_C, Boolean.FALSE);
  public static final ActionW FLIP = new ActionW("flip", Boolean.FALSE);
  public static final ActionW PRESET = new ActionW("preset", null);
  public static final ActionW LUT_SHAPE = new ActionW("lutShape", "LINEAR");
  public static final ActionW INVERSE_LUT = new ActionW("inverseLut", Boolean.FALSE);
  public static final ActionW FILTER = new ActionW("filter", "None");
  public static final ActionW KO = new ActionW("ko", KeyEvent.VK_K, null);
  public static final ActionW PRINT = new ActionW("print", KeyEvent.VK_P, null);
  public static final ActionW ANNOTATIONS = new ActionW("annotations", KeyEvent.VK_SPACE, null);

  public ActionW(String title, Object defaultValue) {
    super(title, defaultValue);
  }

  public ActionW(String title, int keyCode, Object defaultValue) {
    super(title, keyCode, defaultValue);
  }

  public static ActionW getAction(String command) {
    if (command == null) {
      return NONE;
    }
    String cmd = command.trim();
    if ("drawings".equalsIgnoreCase(cmd) || "drawing".equalsIgnoreCase(cmd)) {
      return DRAW;
    }
    if ("scroll".equalsIgnoreCase(cmd) || "series".equalsIgnoreCase(cmd)) {
      return SCROLL_SERIES;
    }
    for (ActionW a :
        new ActionW[] {
          WINDOW,
          LEVEL,
          WINLEVEL,
          SCROLL_SERIES,
          ZOOM,
          ROTATION,
          PAN,
          CROSSHAIR,
          MEASURE,
          DRAW,
          CONTEXTMENU,
          NONE,
          SYNCH,
          LAYOUT,
          RESET,
          CINE,
          FLIP,
          PRESET,
          LUT_SHAPE,
          INVERSE_LUT,
          FILTER,
          KO,
          PRINT,
          ANNOTATIONS
        }) {
      if (a.isAction(cmd)) {
        return a;
      }
    }
    return NONE;
  }
}
