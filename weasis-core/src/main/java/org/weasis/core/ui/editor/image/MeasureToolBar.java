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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.IllegalComponentStateException;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Measure/draw chrome. Headed D/A/Y/G/B are {@link JToggleButton}s in one {@link ButtonGroup} with
 * mouse-sized hit targets. A press on the A bounds selects angle; G selects rectangle.
 */
public class MeasureToolBar extends WtoolBar implements Toolbar {

  public static final String NAME = "Measure";
  public static final String[] BUTTONS = {"D", "A", "Y", "G", "B"};
  static final Dimension HIT = new Dimension(40, 32);
  static final List<MeasureToolBar> LIVE = new CopyOnWriteArrayList<>();

  private final ButtonGroup group = new ButtonGroup();
  private String selected = MeasureTool.DISTANCE;
  private DefaultView2d<?> view;
  private final List<DefaultView2d<?>> targets = new CopyOnWriteArrayList<>();

  public MeasureToolBar() {
    super(NAME, 11);
    setRollover(false);
    setFloatable(false);
    setLayout(new FlowLayout(FlowLayout.LEADING, 4, 2));
    for (String key : BUTTONS) {
      JToggleButton created = button(key);
      add(created);
      chrome(created);
    }
    LIVE.add(this);
    setSelected(MeasureTool.DISTANCE);
  }

  @Override
  public void updateUI() {
    super.updateUI();
    setRollover(false);
    setFloatable(false);
    setLayout(new FlowLayout(FlowLayout.LEADING, 4, 2));
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    attach(view);
    apply(view);
  }

  public void attach(DefaultView2d<?> view) {
    if (view != null && !targets.contains(view)) {
      targets.add(view);
    }
    if (view != null) {
      view.setMeasureToolBar(this);
    }
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public Graphic newGraphic() {
    return MeasureTool.create(selected);
  }

  public void setSelected(String selected) {
    this.selected = selected == null || selected.isBlank() ? MeasureTool.DISTANCE : selected;
    JToggleButton toggle = toggle(this.selected);
    if (toggle != null) {
      toggle.setSelected(true);
    }
  }

  public String getSelected() {
    return selected;
  }

  public JToggleButton toggle(String key) {
    String id = id(key);
    String letter = MeasureTool.shortcut(key);
    for (Component c : getComponents()) {
      if (c instanceof JToggleButton toggle && named(toggle, id, letter)) {
        return toggle;
      }
    }
    return null;
  }

  static boolean named(JToggleButton toggle, String id, String letter) {
    return id.equals(toggle.getName()) || letter.equals(toggle.getText());
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    String tool = MeasureTool.canonical(selected);
    view.setMeasureToolBar(this);
    view.setMeasureTool(tool);
    view.abandonDrawing();
    if (MeasureTool.drawFamily(tool)) {
      view.getMouseActions().setLeft(MouseActions.DRAW);
    } else {
      view.getMouseActions().setLeft(MouseActions.MEASURE);
    }
  }

  void applyAll() {
    apply(view);
    for (DefaultView2d<?> target : targets) {
      if (target != view) {
        apply(target);
      }
    }
  }

  void arm(JToggleButton hit) {
    Object key = hit.getClientProperty("measure.key");
    setSelected(key == null ? hit.getText() : key.toString());
    applyAll();
  }

  /** Toolkit / glass presses over A/G still select the toggle under the pointer. */
  public static boolean armAt(MouseEvent me) {
    JToggleButton hit = hit(me);
    if (hit == null) {
      return false;
    }
    MeasureToolBar bar =
        (MeasureToolBar) SwingUtilities.getAncestorOfClass(MeasureToolBar.class, hit);
    if (bar == null) {
      return false;
    }
    bar.arm(hit);
    return true;
  }

  static JToggleButton hit(MouseEvent me) {
    if (me == null) {
      return null;
    }
    if (me.getComponent() instanceof JToggleButton toggle && barOf(toggle) != null) {
      return toggle;
    }
    return screenHit(me);
  }

  static MeasureToolBar barOf(Component c) {
    return (MeasureToolBar) SwingUtilities.getAncestorOfClass(MeasureToolBar.class, c);
  }

  static JToggleButton screenHit(MouseEvent me) {
    Point screen = screenPoint(me);
    if (screen != null) {
      return toggleAtScreen(screen);
    }
    if (me.getComponent() instanceof MeasureToolBar bar) {
      Component at = bar.getComponentAt(me.getPoint());
      return at instanceof JToggleButton toggle ? toggle : null;
    }
    return null;
  }

  static Point screenPoint(MouseEvent me) {
    try {
      return me.getLocationOnScreen();
    } catch (IllegalComponentStateException e) {
      return null;
    }
  }

  static JToggleButton toggleAtScreen(Point screen) {
    for (MeasureToolBar bar : LIVE) {
      JToggleButton toggle = bar.toggleAt(screen);
      if (toggle != null) {
        return toggle;
      }
    }
    return null;
  }

  JToggleButton toggleAt(Point screen) {
    for (Component c : getComponents()) {
      if (c instanceof JToggleButton toggle && containsScreen(toggle, screen)) {
        return toggle;
      }
    }
    return null;
  }

  static boolean containsScreen(JComponent c, Point screen) {
    if (c == null || screen == null || !c.isShowing()) {
      return false;
    }
    try {
      return new Rectangle(c.getLocationOnScreen(), c.getSize()).contains(screen);
    } catch (IllegalComponentStateException e) {
      return false;
    }
  }

  private JToggleButton button(String key) {
    JToggleButton button = new JToggleButton(key);
    button.setName(id(key));
    button.getAccessibleContext().setAccessibleName(id(key));
    button.putClientProperty("measure.key", key);
    button.setToolTipText(tip(key));
    chrome(button);
    button.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              arm(button);
            }
          }

          @Override
          public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
              arm(button);
            }
          }
        });
    group.add(button);
    return button;
  }

  static void chrome(JToggleButton button) {
    button.setFocusable(false);
    button.setFocusPainted(false);
    button.setBorderPainted(true);
    button.setContentAreaFilled(true);
    button.setOpaque(true);
    button.setMargin(new Insets(4, 10, 4, 10));
    button.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.DARK_GRAY),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));
    button.setMinimumSize(HIT);
    button.setPreferredSize(HIT);
    button.setMaximumSize(HIT);
  }

  public static String id(String key) {
    return switch (MeasureTool.shortcut(key)) {
      case "A" -> "measure-angle";
      case "Y" -> "measure-polyline";
      case "G" -> "measure-rect";
      case "B" -> "measure-textbox";
      default -> "measure-distance";
    };
  }

  static String tip(String key) {
    return switch (key) {
      case "A" -> "Angle";
      case "Y" -> "Polyline";
      case "G" -> "Draw";
      case "B" -> "Textbox";
      default -> "Distance";
    };
  }

  /** Mouse press-release at the button center. Not {@code doClick()}. */
  public static void pressRelease(AbstractButton button) {
    if (button == null) {
      return;
    }
    layoutButton(button);
    int x = Math.max(1, button.getWidth() / 2);
    int y = Math.max(1, button.getHeight() / 2);
    button.dispatchEvent(
        new MouseEvent(
            button,
            MouseEvent.MOUSE_PRESSED,
            0L,
            MouseEvent.BUTTON1_DOWN_MASK,
            x,
            y,
            1,
            false,
            MouseEvent.BUTTON1));
    button.dispatchEvent(
        new MouseEvent(
            button, MouseEvent.MOUSE_RELEASED, 0L, 0, x, y, 1, false, MouseEvent.BUTTON1));
  }

  static void layoutButton(AbstractButton button) {
    if (button.getWidth() > 0 && button.getHeight() > 0) {
      return;
    }
    if (button.getParent() instanceof JComponent parent) {
      parent.setSize(parent.getPreferredSize());
      parent.doLayout();
    }
    if (button.getWidth() <= 0 || button.getHeight() <= 0) {
      button.setSize(HIT);
    }
  }

  @Override
  public JComponent getComponent() {
    return this;
  }
}
