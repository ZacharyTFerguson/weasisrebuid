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

import org.junit.jupiter.api.Test;

class WeasisInfoCommandTest {

  @Test
  void versionFlagPrintsWeasisVersion() {
    String previous = System.getProperty("weasis.version");
    System.setProperty("weasis.version", "4.7.3");
    try {
      WeasisInfoCommand command = new WeasisInfoCommand();
      assertEquals("4.7.3", command.info("-v"));
      assertTrue(command.info("-a").contains("Felix"));
      assertTrue(command.info("--help").contains("Usage: weasis:info"));
    } finally {
      if (previous == null) {
        System.clearProperty("weasis.version");
      } else {
        System.setProperty("weasis.version", previous);
      }
    }
  }
}
