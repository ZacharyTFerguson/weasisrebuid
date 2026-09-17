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

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;

/**
 * Prefs &gt; Draw. Documented 2D measure tools (SHORTCUTS.md D/A/Y/G/B). No invented persist keys.
 */
public class GraphicPrefView extends AbstractItemDialogPage {

  public static final String TITLE = "Draw";

  private final JComboBox<String> tools;

  public GraphicPrefView() {
    super(TITLE, 400);
    tools = new JComboBox<>(MeasureTool.NAMES.toArray(String[]::new));
    tools.setSelectedItem(MeasureTool.DISTANCE);
    JPanel form = new JPanel();
    form.add(new JLabel("Measure tool"));
    form.add(tools);
    add(form);
  }

  public String selectedTool() {
    Object value = tools.getSelectedItem();
    return value == null ? MeasureTool.DISTANCE : value.toString();
  }

  public void setSelectedTool(String tool) {
    tools.setSelectedItem(namedTool(tool));
  }

  static String namedTool(String tool) {
    if (tool == null || !MeasureTool.NAMES.contains(tool)) {
      return MeasureTool.DISTANCE;
    }
    return tool;
  }

  @Override
  public void closeAdditionalWindow() {
    // SHORTCUTS.md lists tools; PREFERENCES.md has no draw persist key.
  }

  @Override
  public void resetToDefaultValues() {
    tools.setSelectedItem(MeasureTool.DISTANCE);
  }
}
