/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.pref;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.editor.image.MouseActions;

/**
 * 2D viewer preferences. Tokens are the documented PREFERENCES.md keys for W/L and PR overlay (no
 * invented JSON keys such as gosh.port).
 */
public class ViewerPrefView extends AbstractItemDialogPage {

  public static final String TITLE = "2D Viewer";
  public static final String PREF_LEVEL_INVERSE = "weasis.level.inverse";
  public static final String PREF_COLOR_WL_APPLY = "weasis.color.wl.apply";
  public static final String PREF_APPLY_LATEST_PR = "weasis.apply.latest.pr";
  public static final String PREF_MOUSE_LEFT = "weasis.toolbar.mouse.left";
  public static final String PREF_MOUSE_MIDDLE = "weasis.toolbar.mouse.middle";
  public static final String PREF_MOUSE_RIGHT = "weasis.toolbar.mouse.right";
  public static final String PREF_MOUSE_WHEEL = "weasis.toolbar.mouse.wheel";

  public static final String[] MOUSE_TOKENS = {
    MouseActions.WINLEVEL,
    MouseActions.PAN,
    MouseActions.ZOOM,
    MouseActions.SCROLL,
    MouseActions.SEQUENCE,
    MouseActions.ROTATION,
    MouseActions.MEASURE,
    MouseActions.CROSSHAIR,
    MouseActions.NONE
  };

  private final WProperties prefs;
  private final JCheckBox inverseBox;
  private final JCheckBox colorWlBox;
  private final JCheckBox latestPrBox;
  private final JComboBox<String> leftMouse;
  private final JComboBox<String> middleMouse;
  private final JComboBox<String> rightMouse;
  private final JComboBox<String> wheelMouse;

  public ViewerPrefView() {
    this(new WProperties());
  }

  public ViewerPrefView(WProperties prefs) {
    super(TITLE, 410);
    this.prefs = prefs == null ? new WProperties() : prefs;
    inverseBox =
        new JCheckBox(
            "Inverse W/L",
            this.prefs.getBooleanProperty(
                PREF_LEVEL_INVERSE, defaultBoolean(PREF_LEVEL_INVERSE, true)));
    inverseBox.setName("levelInverse");
    colorWlBox =
        new JCheckBox(
            "Apply W/L to color",
            this.prefs.getBooleanProperty(
                PREF_COLOR_WL_APPLY, defaultBoolean(PREF_COLOR_WL_APPLY, true)));
    colorWlBox.setName("colorWlApply");
    latestPrBox =
        new JCheckBox(
            "Apply latest PR overlay",
            this.prefs.getBooleanProperty(
                PREF_APPLY_LATEST_PR, defaultBoolean(PREF_APPLY_LATEST_PR, false)));
    latestPrBox.setName("applyLatestPr");
    leftMouse = mouseCombo(PREF_MOUSE_LEFT, MouseActions.WINLEVEL);
    leftMouse.setName("mouseLeft");
    middleMouse = mouseCombo(PREF_MOUSE_MIDDLE, MouseActions.PAN);
    middleMouse.setName("mouseMiddle");
    rightMouse = mouseCombo(PREF_MOUSE_RIGHT, MouseActions.ZOOM);
    rightMouse.setName("mouseRight");
    wheelMouse = mouseCombo(PREF_MOUSE_WHEEL, MouseActions.SCROLL);
    wheelMouse.setName("mouseWheel");
    JPanel form = new JPanel();
    form.add(inverseBox);
    form.add(colorWlBox);
    form.add(latestPrBox);
    form.add(new JLabel("Left mouse"));
    form.add(leftMouse);
    form.add(new JLabel("Middle mouse"));
    form.add(middleMouse);
    form.add(new JLabel("Right mouse"));
    form.add(rightMouse);
    form.add(new JLabel("Wheel"));
    form.add(wheelMouse);
    add(form);
  }

  public boolean levelInverse() {
    return inverseBox.isSelected();
  }

  public void setLevelInverse(boolean inverse) {
    inverseBox.setSelected(inverse);
  }

  public boolean colorWlApply() {
    return colorWlBox.isSelected();
  }

  public void setColorWlApply(boolean apply) {
    colorWlBox.setSelected(apply);
  }

  public boolean applyLatestPr() {
    return latestPrBox.isSelected();
  }

  public void setApplyLatestPr(boolean apply) {
    latestPrBox.setSelected(apply);
  }

  public String mouseLeft() {
    return selected(leftMouse, MouseActions.WINLEVEL);
  }

  public void setMouseLeft(String token) {
    leftMouse.setSelectedItem(MouseActions.normalize(token));
  }

  public String mouseMiddle() {
    return selected(middleMouse, MouseActions.PAN);
  }

  public void setMouseMiddle(String token) {
    middleMouse.setSelectedItem(MouseActions.normalize(token));
  }

  public String mouseRight() {
    return selected(rightMouse, MouseActions.ZOOM);
  }

  public void setMouseRight(String token) {
    rightMouse.setSelectedItem(MouseActions.normalize(token));
  }

  public String mouseWheel() {
    return selected(wheelMouse, MouseActions.SCROLL);
  }

  public void setMouseWheel(String token) {
    wheelMouse.setSelectedItem(MouseActions.normalize(token));
  }

  public static boolean levelInverseProperty() {
    return Boolean.parseBoolean(System.getProperty(PREF_LEVEL_INVERSE, "true"));
  }

  public static boolean colorWlApplyProperty() {
    return Boolean.parseBoolean(System.getProperty(PREF_COLOR_WL_APPLY, "true"));
  }

  public static boolean applyLatestPrProperty() {
    return Boolean.parseBoolean(System.getProperty(PREF_APPLY_LATEST_PR, "false"));
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.putBooleanProperty(PREF_LEVEL_INVERSE, levelInverse());
    prefs.putBooleanProperty(PREF_COLOR_WL_APPLY, colorWlApply());
    prefs.putBooleanProperty(PREF_APPLY_LATEST_PR, applyLatestPr());
    prefs.setProperty(PREF_MOUSE_LEFT, mouseLeft());
    prefs.setProperty(PREF_MOUSE_MIDDLE, mouseMiddle());
    prefs.setProperty(PREF_MOUSE_RIGHT, mouseRight());
    prefs.setProperty(PREF_MOUSE_WHEEL, mouseWheel());
    System.setProperty(PREF_LEVEL_INVERSE, Boolean.toString(levelInverse()));
    System.setProperty(PREF_COLOR_WL_APPLY, Boolean.toString(colorWlApply()));
    System.setProperty(PREF_APPLY_LATEST_PR, Boolean.toString(applyLatestPr()));
    System.setProperty(PREF_MOUSE_LEFT, mouseLeft());
    System.setProperty(PREF_MOUSE_MIDDLE, mouseMiddle());
    System.setProperty(PREF_MOUSE_RIGHT, mouseRight());
    System.setProperty(PREF_MOUSE_WHEEL, mouseWheel());
  }

  @Override
  public void resetToDefaultValues() {
    inverseBox.setSelected(true);
    colorWlBox.setSelected(true);
    latestPrBox.setSelected(false);
    leftMouse.setSelectedItem(MouseActions.WINLEVEL);
    middleMouse.setSelectedItem(MouseActions.PAN);
    rightMouse.setSelectedItem(MouseActions.ZOOM);
    wheelMouse.setSelectedItem(MouseActions.SCROLL);
  }

  private JComboBox<String> mouseCombo(String key, String fallback) {
    JComboBox<String> combo = new JComboBox<>(MOUSE_TOKENS);
    combo.setSelectedItem(
        MouseActions.normalize(prefs.getProperty(key, System.getProperty(key, fallback))));
    return combo;
  }

  private static String selected(JComboBox<String> combo, String fallback) {
    Object value = combo.getSelectedItem();
    return value == null ? fallback : MouseActions.normalize(value.toString());
  }

  private static boolean defaultBoolean(String key, boolean documented) {
    return Boolean.parseBoolean(System.getProperty(key, Boolean.toString(documented)));
  }
}
