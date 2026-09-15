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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import javax.swing.KeyStroke;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.ShortcutManager;
import org.weasis.core.ui.model.layer.AbstractInfoLayer;
import org.weasis.core.ui.model.layer.AbstractInfoLayer.Visibility;

class AnnotationVisibilityHaveTest {

  @Test
  void spaceAndICycleThreeStates() {
    DefaultView2d<?> view = new DefaultView2d<>();
    AbstractInfoLayer layer = view.getInfoLayer();
    assertEquals(Visibility.FULL, layer.getVisibility());
    assertTrue(layer.isFull());
    view.getEventManager()
        .keyPressed(new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SPACE, ' '));
    assertEquals(Visibility.MINIMAL, layer.getVisibility());
    assertTrue(layer.isMinimal());
    view.getEventManager()
        .keyPressed(new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_I, 'i'));
    assertEquals(Visibility.HIDDEN, layer.getVisibility());
    assertFalse(layer.isVisible());
    layer.cycle();
    assertEquals(Visibility.FULL, layer.getVisibility());
  }

  @Test
  void shortcutManagerBindsSpaceAndI() {
    ShortcutManager shortcuts = new ShortcutManager();
    assertEquals(
        ActionW.ANNOTATIONS, shortcuts.getAction(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)));
    assertEquals(ActionW.ANNOTATIONS, shortcuts.getAction(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0)));
    assertEquals(ActionW.ANNOTATIONS, ActionW.getAction("annotations"));
  }
}
