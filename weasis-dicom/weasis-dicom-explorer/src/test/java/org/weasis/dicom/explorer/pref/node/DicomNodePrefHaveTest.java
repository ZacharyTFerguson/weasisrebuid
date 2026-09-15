/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

class DicomNodePrefHaveTest {

  @Test
  void defaultNodeIsDcm4cheePort11112() {
    AbstractDicomNode node = new AbstractDicomNode();
    assertEquals("DCM4CHEE", node.aeTitle());
    assertEquals("localhost", node.host());
    assertEquals(11112, node.port());
    assertEquals("DCM4CHEE@localhost:11112", node.endpoint());
    assertFalse(node.dicomWeb());
  }

  @Test
  void listViewSeedsDefaultAndAcceptsAddedNodes() {
    DicomNodeListView view = new DicomNodeListView();
    assertEquals(1, view.nodes().size());
    AbstractDicomNode seeded = view.nodes().getFirst();
    assertEquals("DCM4CHEE", seeded.aeTitle());
    assertEquals(11112, seeded.port());
    view.select(0);
    assertSame(seeded, view.selected());

    DefaultDicomNode extra = new DefaultDicomNode("pacs", "ORTHANC", "127.0.0.1", 4242);
    view.addNode(extra);
    assertEquals(2, view.nodes().size());
    assertTrue(view.removeNode(extra));
    assertEquals(1, view.nodes().size());
    view.resetToDefaultValues();
    assertEquals("DCM4CHEE", view.nodes().getFirst().aeTitle());
    assertEquals(11112, view.nodes().getFirst().port());
  }

  @Test
  void factoryCreatesNodeListPage() {
    DicomNodePrefFactory factory = new DicomNodePrefFactory();
    AbstractItemDialogPage page = factory.createInstance(null);
    assertInstanceOf(DicomNodeListView.class, page);
    assertEquals(Insertable.Type.PREFERENCES, factory.getType());
    assertTrue(factory.isComponentCreatedByThisFactory(page));
    assertEquals(DicomNodeListView.TITLE, page.getTitle());
  }
}
