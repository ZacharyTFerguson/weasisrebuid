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

import org.junit.jupiter.api.Test;
import org.weasis.core.ui.editor.image.SynchData;
import org.weasis.core.ui.editor.image.SynchView;

class DicomSynchManagerTest {

  @Test
  void frameOfReferenceDoesNotMoveManualPeer() {
    DicomSynchManager mgr = new DicomSynchManager();
    View2d a = new View2d();
    View2d b = new View2d();
    View2d c = new View2d();
    a.setFrameOfReferenceUID("1.2.840.for");
    b.setFrameOfReferenceUID("1.2.840.for");
    c.setFrameOfReferenceUID("1.2.840.other");
    a.getSynchData().setKind(SynchData.Kind.FRAME_OF_REFERENCE);
    b.getSynchData().setKind(SynchData.Kind.FRAME_OF_REFERENCE);
    c.getSynchData().setKind(SynchData.Kind.MANUAL);
    a.setSynch(SynchView.STACK);
    b.setSynch(SynchView.STACK);
    c.setSynch(SynchView.STACK);
    mgr.add(a);
    mgr.add(b);
    mgr.add(c);
    a.setSynchManager(mgr);
    b.setSynchManager(mgr);
    c.setSynchManager(mgr);
    a.setFrameIndex(7);
    assertEquals(7, b.getFrameIndex());
    assertEquals(0, c.getFrameIndex());
  }

  @Test
  void manualIgnoresDifferentFor() {
    DicomSynchManager mgr = new DicomSynchManager();
    View2d a = new View2d();
    View2d b = new View2d();
    a.setFrameOfReferenceUID("for-a");
    b.setFrameOfReferenceUID("for-b");
    a.getSynchData().setKind(SynchData.Kind.MANUAL);
    b.getSynchData().setKind(SynchData.Kind.MANUAL);
    a.setSynch(SynchView.STACK);
    b.setSynch(SynchView.STACK);
    mgr.add(a);
    mgr.add(b);
    a.setSynchManager(mgr);
    b.setSynchManager(mgr);
    a.setFrameIndex(3);
    assertEquals(3, b.getFrameIndex());
    assertFalse(a.getFrameOfReferenceUID().equals(b.getFrameOfReferenceUID()));
  }
}
