/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.gui.central.meta.panel.PersonNameCellEditor;
import org.weasis.acquire.explorer.gui.central.meta.panel.PersonNameView;

class PersonNameEditorViewHaveTest {

  @Test
  void personNameViewMatchesPersonNameEditorComposeValidateAndPreview() {
    PersonNameView view = new PersonNameView();
    PersonNameEditor.Components c =
        new PersonNameEditor.Components("Smith", "John", "Q", "Dr", "Jr");
    PersonNameEditor.writeTo(view, c);
    assertEquals(c, PersonNameEditor.fromView(view));
    assertEquals(PersonNameEditor.compose(c), view.composed());
    assertEquals(PersonNameEditor.compose(view), view.composed());
    assertEquals(PersonNameEditor.lexicalDisplay(view.composed()), view.lexicalDisplay());
    assertEquals(PersonNameEditor.previewOverLength(view.composed()), view.isPreviewOverLength());
    assertTrue(PersonNameEditor.isValid(view));
    assertEquals(PersonNameEditor.parse("Smith^John^Q^Dr^Jr"), PersonNameEditor.fromView(view));
  }

  @Test
  void cellEditorValueIsPersonNameEditorCompose() {
    PersonNameCellEditor editor = new PersonNameCellEditor();
    PersonNameEditor.writeTo(
        editor.view(), new PersonNameEditor.Components("Doe", "Jane", "", "", ""));
    assertEquals(PersonNameEditor.compose(editor.view()), editor.getCellEditorValue());
    assertTrue(PersonNameEditor.isValid(editor.view()));
    editor.view().setComponents("A=B", "Jane", "", "", "");
    assertFalse(PersonNameEditor.isValid(editor.view()));
    assertFalse(editor.stopCellEditing());
  }

  @Test
  void worklistInboundUsesApplyInboundWithoutReformat() {
    String inbound = "Yamada^Tarou=山田^太郎=やまだ^たろう";
    assertEquals(inbound, PersonNameEditor.applyInbound(inbound, true));
    PersonNameView view = new PersonNameView();
    view.setPersonName(PersonNameEditor.applyInbound(inbound, true), true);
    assertEquals(inbound, PersonNameEditor.compose(view));
    assertTrue(PersonNameEditor.lexicalDisplay(view.composed()).startsWith("Yamada, Tarou="));
  }
}
