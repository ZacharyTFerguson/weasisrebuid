/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.util.Toolbar;

/**
 * Key Object chrome. Star toggles the current SOP as a key image (shortcut K); filter shows only
 * key images.
 */
public class KeyObjectToolBar implements Toolbar {

  public static final String NAME = "Key Object";
  public static final String STAR = "star";
  public static final String FILTER = "filter";

  private KOManager manager;
  private final JToolBar bar = new JToolBar(NAME);
  private int position = 50;
  private boolean enabled = true;
  private View2d view;

  public KeyObjectToolBar() {
    this(new KOManager());
  }

  public KeyObjectToolBar(KOManager manager) {
    this.manager = manager == null ? new KOManager() : manager;
    bar.add(button(STAR));
    bar.add(button(FILTER));
  }

  public void bind(View2d view) {
    this.view = view;
    if (view != null) {
      this.manager = view.getKoManager();
    }
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
    return ko.isFilterKeyImages();
  }

  public boolean isFilterKeyImages() {
    return getManager().isFilterKeyImages();
  }

  private JButton button(String name) {
    JButton button =
        new JButton(
            new AbstractAction(name) {
              @Override
              public void actionPerformed(ActionEvent e) {
                if (STAR.equals(name)) {
                  star();
                } else if (FILTER.equals(name)) {
                  filter();
                }
              }
            });
    button.setName(name);
    button.setToolTipText(name);
    return button;
  }

  @Override
  public JComponent getComponent() {
    return bar;
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOLBAR;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return enabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
