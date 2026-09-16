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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;

public class ToolBarContainer extends JPanel {
  public ToolBarContainer() {
    super(new WrapFlow());
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

  /**
   * FlowLayout preferred-height uses unbounded width, so BorderLayout.NORTH clips wrapped bars (Key
   * Object Star/Filter after Viewer). Measure height against the real parent width.
   */
  static final class WrapFlow extends FlowLayout {
    WrapFlow() {
      super(LEADING, 0, 0);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
      return wrapSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
      return wrapSize(target, false);
    }

    Dimension wrapSize(Container target, boolean pref) {
      synchronized (target.getTreeLock()) {
        return measured(target, pref, wrapWidth(target));
      }
    }

    static int wrapWidth(Container target) {
      if (target.getWidth() > 0) {
        return target.getWidth();
      }
      Container parent = target.getParent();
      if (parent != null && parent.getWidth() > 0) {
        return parent.getWidth();
      }
      return Integer.MAX_VALUE;
    }

    Dimension measured(Container target, boolean pref, int max) {
      int x = 0;
      int y = 0;
      int rowH = 0;
      int wide = 0;
      int hgap = getHgap();
      int vgap = getVgap();
      for (Component m : target.getComponents()) {
        if (!m.isVisible()) {
          continue;
        }
        Dimension d = pref ? m.getPreferredSize() : m.getMinimumSize();
        if (needWrap(x, d.width, max)) {
          y += rowH + vgap;
          x = 0;
          rowH = 0;
        }
        x += d.width + hgap;
        wide = Math.max(wide, x);
        rowH = Math.max(rowH, d.height);
      }
      Insets in = target.getInsets();
      return new Dimension(wide + in.left + in.right, y + rowH + in.top + in.bottom);
    }

    static boolean needWrap(int x, int width, int max) {
      return x > 0 && max < Integer.MAX_VALUE && x + width > max;
    }
  }
}
