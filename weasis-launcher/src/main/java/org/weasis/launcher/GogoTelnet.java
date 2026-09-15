/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Felix Gogo 1.1.x exposes telnet via {@code telnetd}, not a {@code gosh.port} pref. Weasis maps
 * the VM property {@code gosh.port} (default 17179) onto that command (MX-16).
 */
final class GogoTelnet {

  private static final Logger LOGGER = LoggerFactory.getLogger(GogoTelnet.class);

  private GogoTelnet() {}

  static void start(Framework framework, String port) throws Exception {
    Object session = newSession(framework);
    Method execute = session.getClass().getMethod("execute", CharSequence.class);
    String command = "telnetd --ip=127.0.0.1 --port=" + port + " start";
    LOGGER.info("Starting Gogo {}", command);
    Exception last = null;
    for (int i = 0; i < 40; i++) {
      try {
        Object result = execute.invoke(session, command);
        LOGGER.info("Gogo telnet: {}", result);
        return;
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (alreadyRunning(cause, port)) {
          LOGGER.info("Gogo telnetd already bound on {}", port);
          return;
        }
        last = e;
        Thread.sleep(50);
      } catch (Exception e) {
        if (alreadyRunning(e, port)) {
          LOGGER.info("Gogo telnetd already bound on {}", port);
          return;
        }
        last = e;
        Thread.sleep(50);
      }
    }
    throw new IllegalStateException("Cannot start Gogo telnetd on " + port, last);
  }

  static void executeLines(Framework framework, List<String> gogoLines) throws Exception {
    if (gogoLines == null || gogoLines.isEmpty()) {
      return;
    }
    Object session = newSession(framework);
    Method execute = session.getClass().getMethod("execute", CharSequence.class);
    executeEach(execute, session, gogoLines);
  }

  private static void executeEach(Method execute, Object session, List<String> gogoLines)
      throws Exception {
    for (String line : gogoLines) {
      executeOne(execute, session, line);
    }
  }

  private static void executeOne(Method execute, Object session, String line) throws Exception {
    if (line == null || line.isBlank()) {
      return;
    }
    execute.invoke(session, line);
  }

  private static Object newSession(Framework framework) throws Exception {
    Object processor = waitForProcessor(framework.getBundleContext());
    Method createSession =
        processor
            .getClass()
            .getMethod("createSession", InputStream.class, OutputStream.class, OutputStream.class);
    return createSession.invoke(processor, InputStream.nullInputStream(), System.out, System.err);
  }

  private static Object waitForProcessor(BundleContext context) throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      ServiceReference<?> ref =
          context.getServiceReference("org.apache.felix.service.command.CommandProcessor");
      if (ref != null) {
        Object processor = context.getService(ref);
        if (processor != null) {
          return processor;
        }
      }
      Thread.sleep(50);
    }
    throw new IllegalStateException("Gogo CommandProcessor is not registered");
  }

  private static boolean alreadyRunning(Throwable t, String port) {
    while (t != null) {
      String message = t.getMessage();
      if (message != null && message.contains("already running") && message.contains(port)) {
        return true;
      }
      t = t.getCause();
    }
    return false;
  }
}
