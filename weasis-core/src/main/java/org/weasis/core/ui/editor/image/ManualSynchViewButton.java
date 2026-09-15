/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import javax.swing.JToggleButton;

/** Selects manual synch group (MX-14). Distinct from {@link SynchViewButton}. */
public class ManualSynchViewButton extends JToggleButton {

  public ManualSynchViewButton() {
    super("Manual");
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.getSynchData().setKind(SynchData.Kind.MANUAL);
    setSelected(true);
  }
}
