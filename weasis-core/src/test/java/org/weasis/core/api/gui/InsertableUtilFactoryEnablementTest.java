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

class InsertableUtilFactoryEnablementTest {

  @Test
  void missingPropertyMeansEnabled() {
    System.clearProperty("org.weasis.dicom.explorer.DicomExplorerFactory");
    assertTrue(InsertableUtil.isFactoryEnabled("org.weasis.dicom.explorer.DicomExplorerFactory"));
  }

  @Test
  void falseDisables() {
    System.setProperty("org.weasis.base.explorer.DefaultExplorerFactory", "false");
    try {
      assertFalse(
          InsertableUtil.isFactoryEnabled("org.weasis.base.explorer.DefaultExplorerFactory"));
    } finally {
      System.clearProperty("org.weasis.base.explorer.DefaultExplorerFactory");
    }
  }
}
