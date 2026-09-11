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

import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

/** Runs work on the EDT unless the VM is headless. */
public final class GuiExecutor {

  private GuiExecutor() {}

  public static void execute(Runnable runnable) {
    if (runnable == null) {
      return;
    }
    if (GraphicsEnvironment.isHeadless() || SwingUtilities.isEventDispatchThread()) {
      runnable.run();
      return;
    }
    SwingUtilities.invokeLater(runnable);
  }

  public static void invokeAndWait(Runnable runnable) {
    if (runnable == null) {
      return;
    }
    if (GraphicsEnvironment.isHeadless() || SwingUtilities.isEventDispatchThread()) {
      runnable.run();
      return;
    }
    try {
      SwingUtilities.invokeAndWait(runnable);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (InvocationTargetException e) {
      throw new IllegalStateException(e.getCause());
    }
  }
}
