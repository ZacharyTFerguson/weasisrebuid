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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Hashtable;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.dicom.codec.DicomMime;

class View2dFactoryTest {

  @Test
  void readsImageDicomAndCreatesContainer() {
    View2dFactory factory = new View2dFactory();
    assertTrue(factory.canReadMimeType(DicomMime.IMAGE_DICOM));
    assertTrue(factory.canReadMimeType(DicomMime.SERIES_DICOM));
    assertEquals(10, factory.getLevel());
    SeriesViewer<?> viewer = factory.createSeriesViewer(new Hashtable<>());
    assertInstanceOf(View2dContainer.class, viewer);
    assertTrue(factory.isViewerCreatedByThisFactory(viewer));
    UICore core = new UICore();
    core.registerSeriesViewerFactory(factory);
    assertTrue(core.getViewerFactory(DicomMime.IMAGE_DICOM).isPresent());
  }
}
