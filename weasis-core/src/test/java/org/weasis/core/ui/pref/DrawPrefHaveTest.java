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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;

class DrawPrefHaveTest {

  @Test
  void factoriesCreateDrawAndShortcutPages() {
    DrawPrefFactory draw = new DrawPrefFactory();
    ShortcutPrefFactory shortcuts = new ShortcutPrefFactory();
    AbstractItemDialogPage drawPage = draw.createInstance(null);
    AbstractItemDialogPage shortcutPage = shortcuts.createInstance(null);
    assertInstanceOf(DrawPrefView.class, drawPage);
    assertInstanceOf(ShortcutPrefView.class, shortcutPage);
    assertEquals(Insertable.Type.PREFERENCES, draw.getType());
    assertTrue(draw.isComponentCreatedByThisFactory(drawPage));
    assertTrue(shortcuts.isComponentCreatedByThisFactory(shortcutPage));
    assertEquals(DrawPrefView.TITLE, drawPage.getTitle());
    assertEquals(ShortcutPrefView.TITLE, shortcutPage.getTitle());
  }

  @Test
  void drawNestsGraphicAndLabelsWithDocumentedTools() {
    DrawPrefView page = new DrawPrefView();
    assertEquals(2, page.getSubPages().size());
    GraphicPrefView graphic = page.graphicPage();
    LabelsPrefView labels = page.labelsPage();
    assertEquals(GraphicPrefView.TITLE, graphic.getTitle());
    assertEquals(LabelsPrefView.TITLE, labels.getTitle());
    assertEquals(MeasureTool.DISTANCE, graphic.selectedTool());
    assertTrue(MeasureTool.NAMES.contains(MeasureTool.ANGLE));
    assertTrue(MeasureTool.NAMES.contains(MeasureTool.POLYLINE));
    assertTrue(MeasureTool.NAMES.contains(MeasureTool.TEXTBOX));
    graphic.setSelectedTool(MeasureTool.ANGLE);
    assertEquals(MeasureTool.ANGLE, graphic.selectedTool());
    graphic.resetToDefaultValues();
    assertEquals(MeasureTool.DISTANCE, graphic.selectedTool());
    assertEquals(Visibility.FULL, labels.annotationVisibility());
    labels.setAnnotationVisibility(Visibility.MINIMAL);
    DefaultView2d<?> view = new DefaultView2d<>();
    labels.applyTo(view.getInfoLayer());
    assertEquals(Visibility.MINIMAL, view.getInfoLayer().getVisibility());
    labels.resetToDefaultValues();
    assertEquals(Visibility.FULL, labels.annotationVisibility());
  }

  @Test
  void shortcutsMapCineNotClipboardAndMeasureIsM() {
    ShortcutPrefView page = new ShortcutPrefView();
    assertSame(ActionW.CINE, page.actionFor(KeyEvent.VK_C));
    assertSame(ActionW.MEASURE, page.actionFor(KeyEvent.VK_M));
    assertSame(ActionW.MEASURE, page.actionFor(KeyEvent.VK_D));
    assertSame(ActionW.DRAW, page.actionFor(KeyEvent.VK_B));
    assertSame(ActionW.RESET, page.actionFor(KeyEvent.VK_ESCAPE));
    assertSame(ActionW.PRESET, page.actionFor(KeyEvent.VK_0));
    assertSame(ActionW.PAN, page.actionFor(KeyEvent.VK_T));
    assertSame(ActionW.WINLEVEL, page.actionFor(KeyEvent.VK_W));
    assertSame(ActionW.ANNOTATIONS, page.actionFor(KeyEvent.VK_SPACE));
    assertSame(ActionW.ANNOTATIONS, page.actionFor(KeyEvent.VK_I));
    assertNull(page.actionFor(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)));
    assertEquals("shortcut-rows", named(page, "shortcut-rows").getName());
    assertEquals("shortcut-table", named(page, "shortcut-table").getName());
    assertInstanceOf(JTextArea.class, named(page, "shortcut-table"));
    assertTrue(page.listedRows().stream().anyMatch(r -> r.contains(ActionW.CINE.cmd())));
    assertTrue(page.listedRows().stream().anyMatch(r -> r.contains(ActionW.MEASURE.cmd())));
    assertTrue(page.listedRows().stream().anyMatch(r -> r.contains("distance")));
    assertTrue(page.listedRows().stream().anyMatch(r -> r.startsWith("Esc reset")));
    assertTrue(page.tableText().contains("T " + ActionW.PAN.cmd()));
    assertTrue(page.tableText().contains("W " + ActionW.WINLEVEL.cmd()));
    assertTrue(page.tableText().contains("C " + ActionW.CINE.cmd()));
    assertTrue(page.tableText().contains("Esc reset"));
    assertTrue(page.tableText().contains("fullscreen"));
    assertTrue(page.tableText().contains("segmentations"));
  }

  static Component named(Component root, String name) {
    if (root == null || name == null) {
      return null;
    }
    if (name.equals(root.getName())) {
      return root;
    }
    if (root instanceof java.awt.Container container) {
      for (Component child : container.getComponents()) {
        Component found = named(child, name);
        if (found != null) {
          return found;
        }
      }
    }
    return null;
  }
}
