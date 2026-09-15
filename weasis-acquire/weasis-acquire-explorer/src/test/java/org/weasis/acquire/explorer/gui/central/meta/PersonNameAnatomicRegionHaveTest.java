/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JTable;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.imp.AcquireGlobalMeta;
import org.weasis.acquire.explorer.gui.central.meta.panel.AnatomicRegionCellEditor;
import org.weasis.acquire.explorer.gui.central.meta.panel.AnatomicRegionView;
import org.weasis.acquire.explorer.gui.central.meta.panel.PersonNameCellEditor;
import org.weasis.acquire.explorer.gui.central.meta.panel.PersonNameView;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireGlobalMetaPanel;

class PersonNameAnatomicRegionHaveTest {

  @Test
  void personNameViewComposesFiveComponentsAndRejectsForbiddenChars() {
    PersonNameView view = new PersonNameView();
    view.setComponents("Smith", "John", "Q", "Dr", "Jr");
    assertEquals("Smith^John^Q^Dr^Jr", view.composed());
    assertEquals("Smith, John", view.lexicalDisplay());
    assertTrue(view.componentsValid());
    assertFalse(view.isPreviewOverLength());
    assertEquals(Color.BLACK, view.previewColor());
    view.setComponents("Sm^ith", "John", "", "", "");
    assertFalse(view.componentsValid());
    view.setComponents("x".repeat(65), "", "", "", "");
    assertTrue(view.isPreviewOverLength());
    assertEquals(Color.RED, view.previewColor());
  }

  @Test
  void worklistInboundIsNotReformattedUntilEdited() {
    PersonNameView view = new PersonNameView();
    String inbound = "Yamada^Tarou=山田^太郎=やまだ^たろう";
    view.setPersonName(inbound, true);
    assertTrue(view.preservesInbound());
    assertEquals(inbound, view.composed());
    assertTrue(view.lexicalDisplay().startsWith("Yamada, Tarou="));
    view.setComponents("Smith", "John", "", "", "");
    assertFalse(view.preservesInbound());
    assertEquals("Smith^John^^^", view.composed());
  }

  @Test
  void personNameCellEditorCommitsComposedPnAndBlocksInvalid() {
    PersonNameCellEditor editor = new PersonNameCellEditor();
    JTable table = new JTable();
    editor.getTableCellEditorComponent(table, "Doe^Jane", true, 0, 1);
    assertEquals("Doe^Jane^^^", editor.getCellEditorValue());
    editor.view().setComponents("Smith", "John", "", "", "");
    assertTrue(editor.stopCellEditing());
    assertEquals("Smith^John^^^", editor.getCellEditorValue());
    editor.view().setComponents("A^B", "John", "", "", "");
    assertFalse(editor.stopCellEditing());
  }

  @Test
  void anatomicRegionWritesCodeAndLabelOnImageAndSeriesMeta() {
    AnatomicRegionView view = new AnatomicRegionView();
    view.setSelectedCode("CHEST");
    assertEquals("CHEST", view.selected().code());
    assertEquals("Chest", view.selected().label());
    AcquireImageInfo image = new AcquireImageInfo();
    Map<String, String> series = new LinkedHashMap<>();
    view.apply(image, series);
    assertEquals("CHEST", image.getAnatomicRegionCode());
    assertEquals("Chest", image.getAnatomicRegionLabel());
    assertEquals("CHEST", series.get(AnatomicRegionView.CODE_TAG));
    assertEquals("Chest", series.get(AnatomicRegionView.LABEL_TAG));
    assertEquals("CHEST", series.get(AnatomicRegionView.BODY_PART_TAG));

    AnatomicRegionCellEditor editor = new AnatomicRegionCellEditor();
    editor.setTag(AnatomicRegionView.BODY_PART_TAG);
    editor.getTableCellEditorComponent(new JTable(), "HAND", true, 0, 1);
    assertEquals("HAND", editor.getCellEditorValue());
    editor.setTag(AnatomicRegionView.LABEL_TAG);
    assertEquals("Hand", editor.getCellEditorValue());
    AcquireImageInfo other = new AcquireImageInfo();
    editor.apply(other, series);
    assertEquals("HAND", other.getAnatomicRegionCode());
    assertEquals("Hand", other.getAnatomicRegionLabel());
  }

  @Test
  void globalPanelUsesPersonNameEditorForPatientNameTag() {
    Properties prefs = new Properties();
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.DISPLAY),
        "PatientName,PatientID");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.EDIT), "PatientName");
    Map<String, String> values = new LinkedHashMap<>();
    values.put("PatientName", "DOE^JANE");
    AcquireGlobalMetaPanel panel = new AcquireGlobalMetaPanel(prefs, values);
    assertEquals("PatientName", panel.table().getValueAt(0, 0));
    PersonNameCellEditor editor = new PersonNameCellEditor();
    editor.getTableCellEditorComponent(panel.table(), panel.table().getValueAt(0, 1), true, 0, 1);
    editor.view().setComponents("Roe", "Ann", "", "", "");
    panel.model().setValueAt(editor.getCellEditorValue(), 0, 1);
    assertEquals("Roe^Ann^^^", values.get("PatientName"));
    assertEquals(
        "MetaValueCellEditor",
        panel.table().getColumnModel().getColumn(1).getCellEditor().getClass().getSimpleName());
    assertTrue(new AcquireGlobalMeta(prefs, values).isCellEditable(0, 1));
  }
}
