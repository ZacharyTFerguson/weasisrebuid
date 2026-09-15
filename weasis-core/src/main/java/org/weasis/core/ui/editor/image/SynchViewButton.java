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

/** Selects Frame of Reference synch (MX-14). */
public class SynchViewButton extends JToggleButton {

  public SynchViewButton() {
    super("FoR");
    setSelected(true);
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.getSynchData().setKind(SynchData.Kind.FRAME_OF_REFERENCE);
    setSelected(true);
  }
}
