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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.KeyStroke;

/** Default 2D map from SHORTCUTS.md (editable under Preferences since 4.7.0). */
public final class ShortcutManager {
  private final Map<KeyStroke, ActionW> map = new LinkedHashMap<>();

  public ShortcutManager() {
    bind(KeyEvent.VK_T, ActionW.PAN);
    bind(KeyEvent.VK_W, ActionW.WINLEVEL);
    bind(KeyEvent.VK_S, ActionW.SCROLL_SERIES);
    bind(KeyEvent.VK_Z, ActionW.ZOOM);
    bind(KeyEvent.VK_R, ActionW.ROTATION);
    bind(KeyEvent.VK_H, ActionW.CROSSHAIR);
    bind(KeyEvent.VK_C, ActionW.CINE);
    bind(KeyEvent.VK_M, ActionW.MEASURE);
    bind(KeyEvent.VK_G, ActionW.DRAW);
    bind(KeyEvent.VK_N, ActionW.NONE);
    bind(KeyEvent.VK_Q, ActionW.CONTEXTMENU);
    bind(KeyEvent.VK_K, ActionW.KO);
    bind(KeyEvent.VK_P, ActionW.PRINT);
    map.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK), ActionW.PRINT);
  }

  public void bind(int keyCode, ActionW action) {
    if (action != null) {
      map.put(KeyStroke.getKeyStroke(keyCode, 0), action);
    }
  }

  public ActionW getAction(KeyStroke stroke) {
    return map.get(stroke);
  }

  public Map<KeyStroke, ActionW> getMap() {
    return Map.copyOf(map);
  }
}
