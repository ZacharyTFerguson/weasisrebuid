/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FactoryEnablementTest {

  @Test
  void falseSystemPropertyDisablesExplicitFactoryKey() {
    String key = "org.weasis.dicom.explorer.DicomExplorerFactory";
    System.setProperty(key, "false");
    try {
      assertFalse(FactoryEnablement.isEnabled(key));
      assertFalse(FactoryEnablement.parseTruthy("false", true));
      assertTrue(FactoryEnablement.parseTruthy("true", false));
    } finally {
      System.clearProperty(key);
    }
    assertTrue(FactoryEnablement.isEnabled(key));
  }
}
