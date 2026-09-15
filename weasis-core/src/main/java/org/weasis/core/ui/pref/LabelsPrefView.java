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
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;

/**
 * Prefs &gt; Labels. Space/I cycle FULL → MINIMAL → HIDDEN (SHORTCUTS.md). No invented persist
 * keys.
 */
public class LabelsPrefView extends AbstractItemDialogPage {

  public static final String TITLE = "Labels";

  private final JComboBox<Visibility> visibility;

  public LabelsPrefView() {
    super(TITLE, 410);
    visibility = new JComboBox<>(Visibility.values());
    visibility.setSelectedItem(Visibility.FULL);
    JPanel form = new JPanel();
    form.add(new JLabel("Annotation overlay"));
    form.add(visibility);
    add(form);
  }

  public Visibility annotationVisibility() {
    Object value = visibility.getSelectedItem();
    return value instanceof Visibility v ? v : Visibility.FULL;
  }

  public void setAnnotationVisibility(Visibility value) {
    visibility.setSelectedItem(value == null ? Visibility.FULL : value);
  }

  public void applyTo(AbstractInfoLayer layer) {
    if (layer != null) {
      layer.setVisibility(annotationVisibility());
    }
  }

  @Override
  public void closeAdditionalWindow() {
    // SHORTCUTS.md documents the three states; PREFERENCES.md has no overlay persist key.
  }

  @Override
  public void resetToDefaultValues() {
    visibility.setSelectedItem(Visibility.FULL);
  }
}
