/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.FactoryEnablement;

class DefaultExplorerFactoryTest {

  @Test
  void factoryHonorsDicomizerDisableKey() {
    String key = DefaultExplorerFactory.class.getName();
    DefaultExplorerFactory factory = new DefaultExplorerFactory();
    assertTrue(factory.isComponentCreatedByThisFactory(factory.createInstance(new Hashtable<>())));
    System.setProperty(key, "false");
    try {
      assertFalse(FactoryEnablement.isEnabled(DefaultExplorerFactory.class));
    } finally {
      System.clearProperty(key);
    }
    assertTrue(FactoryEnablement.isEnabled(DefaultExplorerFactory.class));
  }
}
