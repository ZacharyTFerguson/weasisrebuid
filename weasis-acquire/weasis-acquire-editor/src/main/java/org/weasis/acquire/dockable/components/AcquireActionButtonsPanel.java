/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/** Exclusive rectify / contrast / annotate / calibrate / metadata tool buttons. */
public class AcquireActionButtonsPanel extends JPanel {

  public static final List<String> ACTIONS =
      List.of(
          AcquireActionButton.RECTIFY,
          AcquireActionButton.CONTRAST,
          AcquireActionButton.ANNOTATE,
          AcquireActionButton.CALIBRATE,
          AcquireActionButton.METADATA);

  private final Map<String, AcquireActionButton> buttons = new LinkedHashMap<>();
  private String selected = AcquireActionButton.RECTIFY;

  public AcquireActionButtonsPanel() {
    ButtonGroup group = new ButtonGroup();
    for (String id : ACTIONS) {
      AcquireActionButton button = new AcquireActionButton(id);
      button.addActionListener(e -> select(id));
      group.add(button);
      buttons.put(id, button);
      add(button);
    }
    select(AcquireActionButton.RECTIFY);
  }

  public void select(String actionId) {
    if (!buttons.containsKey(actionId)) {
      return;
    }
    selected = actionId;
    for (Map.Entry<String, AcquireActionButton> entry : buttons.entrySet()) {
      entry.getValue().setSelected(entry.getKey().equals(actionId));
    }
  }

  public String selectedId() {
    return selected;
  }

  public AcquireActionButton selectedButton() {
    return buttons.get(selected);
  }

  public AcquireActionButton button(String actionId) {
    return buttons.get(actionId);
  }
}
