/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import javax.swing.JTabbedPane;

/** Viewer-tab edge: TOP / BOTTOM / LEFT / RIGHT. */
public class TabPlacement {

  public static final int TOP = JTabbedPane.TOP;
  public static final int BOTTOM = JTabbedPane.BOTTOM;
  public static final int LEFT = JTabbedPane.LEFT;
  public static final int RIGHT = JTabbedPane.RIGHT;

  private TabPlacement() {}

  public static int normalize(int placement) {
    if (placement == BOTTOM) {
      return BOTTOM;
    }
    if (placement == LEFT) {
      return LEFT;
    }
    if (placement == RIGHT) {
      return RIGHT;
    }
    return TOP;
  }

  public static void apply(JTabbedPane tabs, int placement) {
    if (tabs != null) {
      tabs.setTabPlacement(normalize(placement));
    }
  }

  public static int of(JTabbedPane tabs) {
    return tabs == null ? TOP : tabs.getTabPlacement();
  }
}
