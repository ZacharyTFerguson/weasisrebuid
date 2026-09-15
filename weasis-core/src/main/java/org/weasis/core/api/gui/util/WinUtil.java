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

import java.awt.Component;
import java.awt.Window;
import javax.swing.SwingUtilities;

public final class WinUtil {
  private WinUtil() {}

  public static Window getParentWindow(Component c) {
    return c == null ? null : SwingUtilities.getWindowAncestor(c);
  }

  public static void center(Window w) {
    if (w != null) {
      w.setLocationRelativeTo(null);
    }
  }
}
