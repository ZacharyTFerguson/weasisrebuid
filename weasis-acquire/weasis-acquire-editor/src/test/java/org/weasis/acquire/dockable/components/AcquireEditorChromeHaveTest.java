/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.weasis.acquire.dockable.components.actions.meta.MetadataAction;
import org.weasis.acquire.explorer.AcquireImageValues;
import org.weasis.acquire.explorer.AcquireMeta;

class AcquireEditorChromeHaveTest {

  @Test
  void actionButtonsAreExclusiveAndCoverEditionTools() {
    AcquireActionButtonsPanel panel = new AcquireActionButtonsPanel();
    assertEquals(AcquireActionButton.RECTIFY, panel.selectedId());
    assertTrue(panel.button(AcquireActionButton.RECTIFY).isSelected());
    panel.select(AcquireActionButton.ANNOTATE);
    assertEquals(AcquireActionButton.ANNOTATE, panel.selectedId());
    assertTrue(panel.button(AcquireActionButton.ANNOTATE).isSelected());
    assertFalse(panel.button(AcquireActionButton.RECTIFY).isSelected());
    panel.select(AcquireActionButton.METADATA);
    assertEquals(AcquireActionButton.METADATA, panel.selectedButton().actionId());
    assertEquals(5, AcquireActionButtonsPanel.ACTIONS.size());
    for (String id : AcquireActionButtonsPanel.ACTIONS) {
      assertEquals(id, panel.button(id).actionId());
    }
  }

  @Test
  void submitApplyCommitsAndCancelRestoresPendingValues() {
    AcquireImageValues values = new AcquireImageValues();
    values.setRotation(0);
    values.setBrightness(1);
    AcquireSubmitButtonsPanel submit = new AcquireSubmitButtonsPanel();
    submit.bind(values);
    values.setRotation(90);
    values.setBrightness(12);
    submit.cancel();
    assertEquals(0, values.getRotation());
    assertEquals(1f, values.getBrightness(), 1e-6f);
    values.setRotation(90);
    submit.apply();
    values.setRotation(180);
    submit.cancel();
    assertEquals(90, values.getRotation());
    assertEquals("apply", submit.applyButton().getName());
    assertEquals("cancel", submit.cancelButton().getName());
  }

  @Test
  void metadataActionRequiredGateUsesGlobalSeriesImageTables() {
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
    Map<String, String> series = new LinkedHashMap<>();
    series.put("Modality", "XC");
    Map<String, String> image = new LinkedHashMap<>();
    MetadataAction action = new MetadataAction();
    action.bind(prefs, global, series, image);
    assertFalse(action.requiredComplete());
    action.panel().global().model().setValueAt("P1", 0, 1);
    assertTrue(action.requiredComplete());
    assertEquals("P1", global.get("PatientID"));
  }
}
