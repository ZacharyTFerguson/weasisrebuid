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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Icon;

public class DefaultAction extends AbstractAction {
  private final Runnable runnable;

  public DefaultAction(String name, Runnable runnable) {
    this(name, null, runnable);
  }

  public DefaultAction(String name, Icon icon, Runnable runnable) {
    super(name, icon);
    this.runnable = runnable;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (runnable != null) {
      runnable.run();
    }
  }
}
