/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Key Object chrome. Star toggles the current SOP as a key image (shortcut K); Filter shows only
 * key images (WP-5 star/filter chrome).
 */
public class KeyObjectToolBar extends WtoolBar implements Toolbar {

  public static final String NAME = "Key Object";
  public static final String STAR = "star";
  public static final String FILTER = "filter";

  private KOManager manager;
  private View2d view;
  private final JButton star = actionButton(STAR, "Star");
  private final JToggleButton filter = new JToggleButton("Filter");

  public KeyObjectToolBar() {
    this(new KOManager());
  }

  public KeyObjectToolBar(KOManager manager) {
    super(NAME, 12);
    this.manager = manager == null ? new KOManager() : manager;
    add(star);
    filter.setName(FILTER);
    filter.setToolTipText("Filter");
    filter.addActionListener(e -> filter());
    add(filter);
  }

  public void bind(View2d view) {
    this.view = view;
    if (view != null) {
      this.manager = view.getKoManager();
    }
    syncFilter();
  }

  public View2d boundView() {
    return view;
  }

  public KOManager getManager() {
    return view != null ? view.getKoManager() : manager;
  }

  public boolean toggle(String sopInstanceUid) {
    return getManager().toggleKeyImage(sopInstanceUid);
  }

  public boolean star() {
    if (view != null) {
      return view.toggleKeyImage();
    }
    return false;
  }

  public boolean filter() {
    KOManager ko = getManager();
    ko.setFilterKeyImages(!ko.isFilterKeyImages());
    if (view != null) {
      view.applyKeyImageFilter();
    }
    syncFilter();
    return ko.isFilterKeyImages();
  }

  public boolean isFilterKeyImages() {
    return getManager().isFilterKeyImages();
  }

  public JToggleButton filterButton() {
    return filter;
  }

  public void syncFilter() {
    filter.setSelected(isFilterKeyImages());
  }

  JButton actionButton(String name, String label) {
    JButton button =
        new JButton(
            new AbstractAction(label) {
              @Override
              public void actionPerformed(ActionEvent e) {
                if (STAR.equals(name)) {
                  star();
                }
              }
            });
    button.setName(name);
    button.setToolTipText(label);
    return button;
  }

  @Override
  public JComponent getComponent() {
    return this;
  }
}
