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

import java.awt.BorderLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.ShortcutManager;

/**
 * Prefs &gt; Shortcuts. Live map from SHORTCUTS.md (T/W/S/Z/R/H/C/M/D/A/Y/B/Esc). Letter C is cine,
 * not clipboard. No invented persist keys.
 */
public class ShortcutPrefView extends ShellPrefPage {

  public static final String TITLE = "Shortcuts";

  private final ShortcutManager shortcuts;
  private final JList<String> rows;
  private final JTextArea table;

  public ShortcutPrefView() {
    this(new ShortcutManager());
  }

  public ShortcutPrefView(ShortcutManager shortcuts) {
    super(TITLE, 600);
    this.shortcuts = shortcuts == null ? new ShortcutManager() : shortcuts;
    String[] data = rowArray();
    rows = new JList<>(data);
    rows.setName("shortcut-rows");
    table = new JTextArea(String.join("\n", data));
    table.setName("shortcut-table");
    table.setEditable(false);
    table.setLineWrap(false);
    JScrollPane scroll = new JScrollPane(table);
    scroll.setName("shortcut-scroll");
    add(scroll, BorderLayout.CENTER);
    add(new JScrollPane(rows), BorderLayout.EAST);
  }

  public ShortcutManager shortcuts() {
    return shortcuts;
  }

  public ActionW actionFor(int keyCode) {
    return shortcuts.getAction(KeyStroke.getKeyStroke(keyCode, 0));
  }

  public ActionW actionFor(KeyStroke stroke) {
    return shortcuts.getAction(stroke);
  }

  public List<String> listedRows() {
    List<String> listed = new ArrayList<>();
    for (int i = 0; i < rows.getModel().getSize(); i++) {
      listed.add(rows.getModel().getElementAt(i));
    }
    return listed;
  }

  public String tableText() {
    return table.getText();
  }

  @Override
  public void closeAdditionalWindow() {
    // SHORTCUTS.md is the Have contract; PREFERENCES.md has no shortcut overlay key.
  }

  @Override
  public void resetToDefaultValues() {
    String[] data = rowArray();
    rows.setListData(data);
    table.setText(String.join("\n", data));
  }

  String[] rowArray() {
    List<String> out = new ArrayList<>();
    shortcuts.getMap().forEach((stroke, action) -> out.add(describe(stroke, action)));
    return out.toArray(String[]::new);
  }

  static String describe(KeyStroke stroke, ActionW action) {
    return keyText(stroke) + " " + actionLabel(stroke, action);
  }

  static String actionLabel(KeyStroke stroke, ActionW action) {
    if (stroke == null) {
      return action == null ? "" : action.cmd();
    }
    if ((stroke.getModifiers() & InputEvent.ALT_DOWN_MASK) != 0
        && stroke.getKeyCode() == KeyEvent.VK_S) {
      return "segmentations";
    }
    return documentedCmd(stroke.getKeyCode(), action);
  }

  static String documentedCmd(int code, ActionW action) {
    return switch (code) {
      case KeyEvent.VK_D -> "distance";
      case KeyEvent.VK_A -> "angle";
      case KeyEvent.VK_Y -> "polyline";
      case KeyEvent.VK_B -> "textbox";
      case KeyEvent.VK_ESCAPE -> "reset";
      case KeyEvent.VK_F11 -> "fullscreen";
      default -> action == null ? "" : action.cmd();
    };
  }

  static String keyText(KeyStroke stroke) {
    if (stroke == null) {
      return "";
    }
    if (stroke.getKeyCode() == KeyEvent.VK_ESCAPE) {
      return "Esc" + modifierSuffix(stroke.getModifiers());
    }
    return KeyEvent.getKeyText(stroke.getKeyCode()) + modifierSuffix(stroke.getModifiers());
  }

  static String modifierSuffix(int modifiers) {
    if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
      return "+Alt";
    }
    return "";
  }
}
