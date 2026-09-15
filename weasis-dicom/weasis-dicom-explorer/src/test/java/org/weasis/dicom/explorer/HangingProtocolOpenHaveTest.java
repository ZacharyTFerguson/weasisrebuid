/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.viewer2d.View2dContainer;
import org.weasis.dicom.viewer2d.View2dFactory;

class HangingProtocolOpenHaveTest {

  @Test
  void dxOpenAppliesOneByTwoAndHangsSecondSeriesInExtraCell() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
    try {
      ViewerPlugin<?> first = opening.open(dx("2.25.dx.pa"));
      assertInstanceOf(View2dContainer.class, first);
      View2dContainer container = (View2dContainer) first;
      assertEquals(2, container.getLayoutCount());
      ViewerPlugin<?> same = opening.open(dx("2.25.dx.lat"));
      assertSame(first, same);
      assertNotSame(
          container.getLayoutViews().get(0).getSeries(),
          container.getLayoutViews().get(1).getSeries());
    } finally {
      for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
        core.closeViewerPlugin(plugin);
      }
      core.unregisterSeriesViewerFactory(factory);
    }
  }

  @Test
  void ctOpenStaysOneByOne() {
    UICore core = new UICore();
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    PluginOpeningStrategy opening = new PluginOpeningStrategy(core);
    try {
      ViewerPlugin<?> opened = opening.open(ct("2.25.ct.s1"));
      assertInstanceOf(View2dContainer.class, opened);
      View2dContainer container = (View2dContainer) opened;
      assertEquals(1, container.getLayoutCount());
    } finally {
      for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
        core.closeViewerPlugin(plugin);
      }
      core.unregisterSeriesViewerFactory(factory);
    }
  }

  static ImportedInstance dx(String seriesUid) {
    return new ImportedInstance(
        "SYNTHETIC^DX",
        "SYN-DX-1",
        "2.25.dx.study",
        seriesUid,
        seriesUid + ".1",
        UID.DigitalXRayImageStorageForPresentation,
        "DX",
        seriesUid,
        "20260101",
        1,
        1,
        null,
        DicomMime.IMAGE_DICOM);
  }

  static ImportedInstance ct(String seriesUid) {
    return new ImportedInstance(
        "SYNTHETIC^CT",
        "SYN-CT-1",
        "2.25.ct.study",
        seriesUid,
        seriesUid + ".1",
        UID.CTImageStorage,
        "CT",
        seriesUid,
        "20260101",
        1,
        1,
        null,
        DicomMime.IMAGE_DICOM);
  }
}
