/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.weasis.core.api.service.UICore;

class WeasisUiCommandHaveTest {

  @Test
  void quitWithoutActivateStillReturnsStopping() throws Exception {
    WeasisUiCommand command = new WeasisUiCommand();
    assertEquals("stopping", command.ui("-q"));
    assertEquals("stopping", command.ui("--quit"));
  }

  @Test
  void quitStopsSystemBundleWhenActivated() throws Exception {
    AtomicBoolean stopped = new AtomicBoolean();
    Bundle system =
        stub(
            Bundle.class,
            (proxy, method, args) -> {
              if ("stop".equals(method.getName())) {
                stopped.set(true);
              }
              return defaultValue(method.getReturnType());
            });
    BundleContext context =
        stub(
            BundleContext.class,
            (proxy, method, args) -> {
              if ("getBundle".equals(method.getName()) && args != null && args.length == 1) {
                return system;
              }
              return defaultValue(method.getReturnType());
            });
    WeasisUiCommand command = new WeasisUiCommand();
    command.activate(context);
    assertEquals("stopping", command.ui("-q"));
    assertTrue(stopped.get());
  }

  @Test
  void visiblePutsApplicationWindowOnTop() throws Exception {
    Assumptions.assumeFalse(GraphicsEnvironment.isHeadless());
    JFrame window = new JFrame("weasis-ui-visible");
    window.setVisible(false);
    window.setExtendedState(JFrame.ICONIFIED);
    UICore core = UICore.getInstance();
    JFrame previous = core.getApplicationWindow();
    core.setApplicationWindow(window);
    try {
      WeasisUiCommand command = new WeasisUiCommand();
      assertEquals("visible", command.ui("-v"));
      assertTrue(window.isVisible());
      assertEquals(0, window.getExtendedState() & JFrame.ICONIFIED);
      window.setVisible(false);
      assertEquals("visible", command.ui("--visible"));
      assertTrue(window.isVisible());
    } finally {
      core.setApplicationWindow(previous);
      window.dispose();
    }
  }

  @Test
  void visibleWithoutWindowReturnsHeadlessNote() throws Exception {
    UICore core = UICore.getInstance();
    JFrame previous = core.getApplicationWindow();
    core.setApplicationWindow(null);
    try {
      WeasisUiCommand command = new WeasisUiCommand();
      assertEquals(GraphicsEnvironmentNote.HEADLESS, command.ui("-v"));
    } finally {
      core.setApplicationWindow(previous);
    }
  }

  @FunctionalInterface
  interface Invoke {
    Object apply(Object proxy, java.lang.reflect.Method method, Object[] args) throws Throwable;
  }

  static <T> T stub(Class<T> type, Invoke invoke) {
    return type.cast(
        Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, invoke::apply));
  }

  static Object defaultValue(Class<?> type) {
    if (!type.isPrimitive()) {
      return null;
    }
    if (type == boolean.class) {
      return false;
    }
    if (type == long.class) {
      return 0L;
    }
    return 0;
  }
}
