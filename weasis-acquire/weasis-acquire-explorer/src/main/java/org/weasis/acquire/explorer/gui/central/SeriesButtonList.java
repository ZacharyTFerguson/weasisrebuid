/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central;

import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

/** Row of exclusive {@link SeriesButton}s for imported series groups. */
public class SeriesButtonList extends JPanel {

  private final Map<String, SeriesButton> buttons = new LinkedHashMap<>();
  private Consumer<String> onSelect = s -> {};
  private String selected;

  public SeriesButtonList() {
    super(new FlowLayout(FlowLayout.LEFT));
  }

  public void setOnSelect(Consumer<String> onSelect) {
    this.onSelect = onSelect == null ? s -> {} : onSelect;
  }

  public void setSeries(List<String> names) {
    removeAll();
    buttons.clear();
    ButtonGroup next = new ButtonGroup();
    if (names != null) {
      for (String name : names) {
        SeriesButton button = new SeriesButton(name);
        button.addActionListener(
            e -> {
              selected = button.series();
              onSelect.accept(selected);
            });
        next.add(button);
        buttons.put(button.series(), button);
        add(button);
      }
    }
    selected = null;
    revalidate();
    repaint();
  }

  public void select(String series) {
    SeriesButton button = buttons.get(series);
    if (button == null) {
      return;
    }
    selected = series;
    button.setSelected(true);
  }

  public String selectedSeries() {
    return selected;
  }

  public SeriesButton button(String series) {
    return buttons.get(series);
  }

  public List<String> seriesNames() {
    return List.copyOf(buttons.keySet());
  }

  public boolean isEmpty() {
    return buttons.isEmpty();
  }
}
