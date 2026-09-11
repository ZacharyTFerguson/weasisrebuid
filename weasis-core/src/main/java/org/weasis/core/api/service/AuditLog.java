/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Application audit logger (user actions). */
public final class AuditLog {

  private static final Logger LOGGER = LoggerFactory.getLogger("AUDIT");

  private AuditLog() {}

  public static void info(String message, Object... args) {
    LOGGER.info(message, args);
  }

  public static void error(String message, Throwable thrown) {
    LOGGER.error(message, thrown);
  }
}
