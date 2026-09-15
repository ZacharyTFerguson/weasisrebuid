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

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;

public class ToolBarContainer extends JPanel {
  public ToolBarContainer() {
    super(new FlowLayout(FlowLayout.LEADING, 0, 0));
  }

  public void registerToolBar(Insertable bar) {
    if (bar instanceof Component component) {
      add(component);
      return;
    }
    if (bar instanceof Toolbar toolbar) {
      add(toolbar.getComponent());
    }
  }

  public void replaceViewerBars(List<Insertable> bars) {
    replaceViewerBars(bars, null);
  }

  public void replaceViewerBars(List<Insertable> bars, Object source) {
    removeInsertables();
    addBars(bars, source);
    revalidate();
    repaint();
  }

  void removeInsertables() {
    List<Component> gone = new ArrayList<>();
    for (Component c : getComponents()) {
      if (c instanceof Insertable) {
        gone.add(c);
      }
    }
    for (Component c : gone) {
      remove(c);
    }
  }

  void addBars(List<Insertable> bars, Object source) {
    if (bars == null) {
      return;
    }
    for (Insertable bar : bars) {
      addBar(bar, source);
    }
  }

  void addBar(Insertable bar, Object source) {
    registerToolBar(bar);
    updateDynamic(bar, source);
  }

  void updateDynamic(Insertable bar, Object source) {
    if (bar instanceof DynamicToolbar dyn) {
      dyn.update(source);
    }
  }
}
