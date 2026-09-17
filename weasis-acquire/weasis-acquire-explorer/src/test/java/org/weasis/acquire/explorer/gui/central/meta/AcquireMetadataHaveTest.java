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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.explorer.AcquireMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.AcquireMetadataTableModel;
import org.weasis.acquire.explorer.gui.central.meta.model.imp.AcquireGlobalMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.imp.AcquireImageMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.imp.AcquireSeriesMeta;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireGlobalMetaPanel;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireImageMetaPanel;
import org.weasis.acquire.explorer.gui.central.meta.panel.imp.AcquireSeriesMetaPanel;

class AcquireMetadataHaveTest {

  @Test
  void displayEditRequiredDriveTableAndCompleteness() {
    Properties prefs = new Properties();
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.DISPLAY),
        "PatientName,PatientID,AccessionNumber");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.EDIT),
        "PatientName,PatientID");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.REQUIRED),
        "PatientName,PatientID");
    Map<String, String> values = new LinkedHashMap<>();
    values.put("PatientName", "DOE^JANE");
    AcquireGlobalMeta model = new AcquireGlobalMeta(prefs, values);
    assertEquals(3, model.getRowCount());
    assertEquals("PatientName", model.getValueAt(0, 0));
    assertEquals("DOE^JANE", model.getValueAt(0, 1));
    assertTrue(model.isCellEditable(0, 1));
    assertTrue(model.isCellEditable(1, 1));
    assertFalse(model.isCellEditable(2, 1));
    assertFalse(model.requiredComplete());
    assertEquals(1, model.missingRequired().size());
    model.setValueAt("ID-1", 1, 1);
    assertEquals("ID-1", values.get("PatientID"));
    assertTrue(model.requiredComplete());
    model.setValueAt("LOCKED", 2, 1);
    assertEquals(null, values.get("AccessionNumber"));
  }

  @Test
  void seriesAndImageScopesUseTheirOwnPrefKeys() {
    Properties prefs = new Properties();
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.SERIES, AcquireMeta.SetKind.DISPLAY),
        "Modality,SeriesDescription");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.SERIES, AcquireMeta.SetKind.EDIT), "SeriesDescription");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.SERIES, AcquireMeta.SetKind.REQUIRED), "Modality");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.IMAGE, AcquireMeta.SetKind.DISPLAY), "ImageComments");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.IMAGE, AcquireMeta.SetKind.EDIT), "ImageComments");
    Map<String, String> series = new LinkedHashMap<>();
    series.put("Modality", "OT");
    AcquireSeriesMeta seriesModel = new AcquireSeriesMeta(prefs, series);
    assertEquals(AcquireMeta.Scope.SERIES, seriesModel.scope());
    assertTrue(seriesModel.requiredComplete());
    assertFalse(seriesModel.isCellEditable(0, 1));
    assertTrue(seriesModel.isCellEditable(1, 1));

    Map<String, String> image = new LinkedHashMap<>();
    AcquireImageMeta imageModel = new AcquireImageMeta(prefs, image);
    assertEquals(1, imageModel.getRowCount());
    imageModel.setValueAt("ruler", 0, 1);
    assertEquals("ruler", image.get("ImageComments"));
  }

  @Test
  void panelsExposeScopeAndRequiredGate() {
    Properties prefs = new Properties();
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.DISPLAY), "PatientID");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.EDIT), "PatientID");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.GLOBAL, AcquireMeta.SetKind.REQUIRED), "PatientID");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.SERIES, AcquireMeta.SetKind.DISPLAY), "Modality");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.SERIES, AcquireMeta.SetKind.REQUIRED), "Modality");
    prefs.setProperty(
        AcquireMeta.key(AcquireMeta.Scope.IMAGE, AcquireMeta.SetKind.DISPLAY), "ImageComments");

    Map<String, String> global = new LinkedHashMap<>();
    AcquireGlobalMetaPanel globalPanel = new AcquireGlobalMetaPanel(prefs, global);
    assertEquals(AcquireMeta.Scope.GLOBAL, globalPanel.scope());
    assertFalse(globalPanel.requiredComplete());
    globalPanel.model().setValueAt("P1", 0, 1);
    assertTrue(globalPanel.requiredComplete());

    Map<String, String> series = new LinkedHashMap<>();
    series.put("Modality", "XC");
    AcquireSeriesMetaPanel seriesPanel = new AcquireSeriesMetaPanel(prefs, series);
    assertEquals(AcquireMeta.Scope.SERIES, seriesPanel.scope());
    assertTrue(seriesPanel.requiredComplete());

    AcquireImageMetaPanel imagePanel = new AcquireImageMetaPanel(prefs, new LinkedHashMap<>());
    assertEquals(AcquireMeta.Scope.IMAGE, imagePanel.scope());
    assertTrue(imagePanel.requiredComplete());
    assertEquals(AcquireMetadataTableModel.TAG_COLUMN, imagePanel.model().getColumnName(0));
  }
}
