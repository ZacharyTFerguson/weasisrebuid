/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.service.WProperties;
import org.weasis.core.ui.model.graphic.GraphicKind;

class KeyObjectSelectionTest {

  @Test
  void starFilterExportAndHiddenToolbar(@TempDir Path dir) throws Exception {
    java.io.File ct = dir.resolve("ct.dcm").toFile();
    TestCt.write(ct, 8, 40, 400);
    View2d view = new View2d();
    view.load(ct);
    String sop = view.getDataset().getString(Tag.SOPInstanceUID);
    KeyObjectSelection ko = new KeyObjectSelection();
    ko.star(sop);
    assertTrue(ko.isStarred(sop));
    assertEquals(List.of(sop), ko.filter(List.of(sop, "2.25.other")));
    assertTrue(ko.canDelete());
    Attributes doc = ko.exportDocument(view.getDataset());
    assertEquals(UID.KeyObjectSelectionDocumentStorage, doc.getString(Tag.SOPClassUID));
    assertTrue(KeyObjectSelection.canDelete(doc));
    KeyObjectSelection loaded = KeyObjectSelection.fromDocument(doc);
    assertTrue(loaded.isStarred(sop));
    java.io.File out = dir.resolve("ko.dcm").toFile();
    ko.exportFile(out, view.getDataset());
    assertTrue(out.length() > 128);
    WProperties prefs = new WProperties();
    assertFalse(KeyObjectSelection.toolbarVisible(prefs));
    prefs.putBooleanProperty(KeyObjectSelection.TOOLBAR_PREF, true);
    assertTrue(KeyObjectSelection.toolbarVisible(prefs));
    DrawMeasureDock dock = new DrawMeasureDock();
    assertEquals(GraphicKind.SELECT, dock.currentTool());
    dock.pick(GraphicKind.LINE);
    dock.afterStroke();
    assertEquals(GraphicKind.SELECT, dock.currentTool());
    dock.setKeepTool(true);
    dock.pick(GraphicKind.LINE);
    dock.afterStroke();
    assertEquals(GraphicKind.LINE, dock.currentTool());
  }
}
