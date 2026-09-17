/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PrintDialogHaveTest {

  @Test
  void imgPagePrintableMapSetsNamedState() {
    PrintDialog dialog = new PrintDialog(null);
    try {
      assertEquals("img-print-dialog", dialog.getName());
      assertEquals("img-page", dialog.pageButton().getName());
      assertEquals("img-print", dialog.printableButton().getName());
      assertEquals("img-print-state", dialog.stateLabel().getName());
      assertEquals("none", dialog.stateText());
      dialog.pageButton().doClick();
      assertEquals("page", dialog.stateText());
      assertFalse(dialog.getOptions().isShowingAnnotations());
      dialog.printableButton().doClick();
      assertEquals("printable", dialog.stateText());
      assertTrue(dialog.getOptions().isShowingAnnotations());
    } finally {
      dialog.dispose();
    }
  }
}
