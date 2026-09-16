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
import java.awt.Graphics;
import java.awt.Graphics2D;
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
 * mouse-sized hit targets. A press on the A bounds selects angle; G selects rectangle. Unselected
 * letters paint the same box family as sunken D. Glass overlays redispatch clicks onto those boxes.
 */
public class MeasureToolBar extends WtoolBar implements Toolbar {

  public static final String NAME = "Measure";
  public static final String[] BUTTONS = {"D", "A", "Y", "G", "B"};
  static final Dimension HIT = new Dimension(40, 32);
  static final Color BOX = Color.LIGHT_GRAY;
  static final String GLASS_MARK = "measure.glass";
  static final List<MeasureToolBar> LIVE = new CopyOnWriteArrayList<>();
  static final MouseAdapter GLASS = glassMouse();

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
      add(button(key));
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
    rechrome();
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    super.addImpl(comp, constraints, index);
    if (comp instanceof JToggleButton toggle) {
      chrome(toggle);
    }
  }

  void rechrome() {
    for (Component c : getComponents()) {
      if (c instanceof JToggleButton toggle) {
        chrome(toggle);
      }
    }
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
    MeasureToolBar bar = barOf(hit);
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
      return absPoint(me);
    }
  }

  static Point absPoint(MouseEvent me) {
    if (me == null) {
      return null;
    }
    return new Point(me.getXOnScreen(), me.getYOnScreen());
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

  static MeasureToolBar barAtScreen(Point screen) {
    for (MeasureToolBar bar : LIVE) {
      if (containsScreen(bar, screen)) {
        return bar;
      }
    }
    return null;
  }

  JToggleButton toggleAt(Point screen) {
    JToggleButton exact = exactToggle(screen);
    return exact != null ? exact : nearbyToggle(screen);
  }

  JToggleButton exactToggle(Point screen) {
    for (Component c : getComponents()) {
      if (c instanceof JToggleButton toggle && containsPad(toggle, screen, 2)) {
        return toggle;
      }
    }
    return null;
  }

  JToggleButton nearbyToggle(Point screen) {
    if (!containsScreen(this, screen)) {
      return null;
    }
    JToggleButton best = null;
    int bestD = Integer.MAX_VALUE;
    for (Component c : getComponents()) {
      if (c instanceof JToggleButton toggle) {
        int d = screenDist(toggle, screen);
        if (d < bestD) {
          bestD = d;
          best = toggle;
        }
      }
    }
    return best;
  }

  static int screenDist(JComponent c, Point screen) {
    Rectangle box = DefaultView2d.liveScreenBox(c);
    if (box == null || screen == null) {
      return Integer.MAX_VALUE;
    }
    int dx = screen.x - (box.x + box.width / 2);
    int dy = screen.y - (box.y + box.height / 2);
    return dx * dx + dy * dy;
  }

  static boolean containsScreen(JComponent c, Point screen) {
    return containsPad(c, screen, 0);
  }

  static boolean containsPad(JComponent c, Point screen, int pad) {
    Rectangle box = DefaultView2d.liveScreenBox(c);
    if (box == null || screen == null) {
      return false;
    }
    if (pad > 0) {
      box.grow(pad, pad);
    }
    return box.contains(screen);
  }

  /**
   * Docking / JFrame glass covers D/A/Y/G/B. Hit-test those toggles first and redispatch press,
   * release, and click onto the button under the screen point.
   */
  public static void installGlass(Component glass) {
    if (!(glass instanceof JComponent jc) || marked(jc)) {
      return;
    }
    jc.putClientProperty(GLASS_MARK, Boolean.TRUE);
    jc.addMouseListener(GLASS);
  }

  static boolean marked(JComponent jc) {
    return Boolean.TRUE.equals(jc.getClientProperty(GLASS_MARK));
  }

  static MouseAdapter glassMouse() {
    return new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        forwardIfFree(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        forwardIfFree(e);
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        forwardIfFree(e);
      }
    };
  }

  static void forwardIfFree(MouseEvent e) {
    if (e != null && !e.isConsumed()) {
      redispatch(e);
    }
  }

  public static boolean redispatch(MouseEvent me) {
    JToggleButton hit = target(me);
    if (hit == null) {
      return false;
    }
    hit.dispatchEvent(translated(me, hit));
    me.consume();
    return true;
  }

  static JToggleButton target(MouseEvent me) {
    if (me == null || alreadyOnToggle(me) || !clickId(me.getID())) {
      return null;
    }
    return toggleAtScreen(screenPoint(me));
  }

  static boolean alreadyOnToggle(MouseEvent me) {
    return me.getComponent() instanceof JToggleButton t && barOf(t) != null;
  }

  static boolean clickId(int id) {
    return id == MouseEvent.MOUSE_PRESSED
        || id == MouseEvent.MOUSE_RELEASED
        || id == MouseEvent.MOUSE_CLICKED;
  }

  static MouseEvent translated(MouseEvent me, Component dest) {
    Point p = localFromScreen(me, dest);
    return new MouseEvent(
        dest,
        me.getID(),
        me.getWhen(),
        me.getModifiersEx(),
        p.x,
        p.y,
        me.getXOnScreen(),
        me.getYOnScreen(),
        me.getClickCount(),
        me.isPopupTrigger(),
        me.getButton());
  }

  static Point localFromScreen(MouseEvent me, Component dest) {
    Point p = new Point(me.getXOnScreen(), me.getYOnScreen());
    if (dest.isShowing()) {
      SwingUtilities.convertPointFromScreen(p, dest);
      return p;
    }
    return new Point(Math.max(1, dest.getWidth() / 2), Math.max(1, dest.getHeight() / 2));
  }

  private JToggleButton button(String key) {
    MeasureToggle button = new MeasureToggle(key);
    button.setName(id(key));
    button.getAccessibleContext().setAccessibleName(id(key));
    button.putClientProperty("measure.key", key);
    button.setToolTipText(tip(key));
    chrome(button);
    button.addMouseListener(pressArm(button));
    group.add(button);
    return button;
  }

  MouseAdapter pressArm(JToggleButton button) {
    return new MouseAdapter() {
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
    };
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
            BorderFactory.createLineBorder(BOX), BorderFactory.createEmptyBorder(4, 10, 4, 10)));
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

  /**
   * Toolbar flattening turns unselected toggles into JLabel-looking letters. Always paint a light
   * box so A/Y/G/B stay the same chrome family as sunken D and remain mouse-sized hit-targets.
   */
  static final class MeasureToggle extends JToggleButton {
    MeasureToggle(String key) {
      super(key);
    }

    @Override
    public void updateUI() {
      super.updateUI();
      chrome(this);
    }

    @Override
    public void setBorderPainted(boolean painted) {
      super.setBorderPainted(true);
    }

    @Override
    public void setContentAreaFilled(boolean filled) {
      super.setContentAreaFilled(true);
    }

    @Override
    public Dimension getPreferredSize() {
      return HIT;
    }

    @Override
    public Dimension getMinimumSize() {
      return HIT;
    }

    @Override
    public Dimension getMaximumSize() {
      return HIT;
    }

    @Override
    public void paint(Graphics g) {
      super.paint(g);
      paintBox(g);
    }

    void paintBox(Graphics g) {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setColor(BOX);
      g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
      if (isSelected()) {
        g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
      }
      g2.dispose();
    }
  }
}
