/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaSeriesGroupNode;
import org.weasis.core.api.media.data.TagW;

class FileModelHaveTest {

  @Test
  void addGroupBuildsPatientStudySeriesTreeAndFiresAdd() {
    FileModel model = new FileModel();
    List<Object> added = new ArrayList<>();
    model.addPropertyChangeListener(evt -> added.add(evt.getNewValue()));
    MediaSeriesGroupNode patient = model.addGroup(null, TagW.PatientID, "P1");
    MediaSeriesGroupNode study = model.addGroup(patient, TagW.StudyInstanceUID, "2.25.st");
    MediaSeriesGroupNode series = model.addGroup(study, TagW.SeriesInstanceUID, "2.25.se");
    assertEquals(1, model.getChildren(model.getRoot()).size());
    assertSame(patient, model.getHierarchyNode(null, "P1"));
    assertSame(study, model.getHierarchyNode(patient, "2.25.st"));
    assertSame(series, model.getHierarchyNode(study, "2.25.se"));
    assertEquals(3, added.size());
    assertTrue(model.removeHierarchyNode(study, series));
    assertTrue(model.getChildren(study).isEmpty());
    assertFalse(model.removeHierarchyNode(study, series));
  }

  @Test
  void duplicateChildIsIgnoredAndStructureHasFourLevels() {
    FileModel model = new FileModel();
    MediaSeriesGroupNode patient = model.addGroup(null, TagW.PatientID, "P1");
    assertFalse(model.addHierarchyNode(null, patient));
    assertEquals(4, model.getModelStructure().getNodes().length);
    assertEquals(TagW.PatientID, model.getModelStructure().getNodes()[0].getTagID());
    assertEquals(TagW.SOPInstanceUID, model.getModelStructure().getNodes()[3].getTagID());
  }
}
