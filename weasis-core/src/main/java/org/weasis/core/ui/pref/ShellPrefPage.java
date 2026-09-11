/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

/** Placeholder prefs page (full widgets land with later WPs). */
public class ShellPrefPage extends AbstractItemDialogPage {

  public ShellPrefPage(String title, int position) {
    super(title, position);
    add(new JLabel(title, SwingConstants.LEADING));
  }

  @Override
  public void closeAdditionalWindow() {
    // no-op shell
  }

  @Override
  public void resetToDefaultValues() {
    // no-op shell
  }
}
