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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import javax.swing.JFrame;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.TagW;
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
      ViewerPlugin<?> first = opening.open(dx("DX", "1", "2.25.dx.pa"));
      assertInstanceOf(View2dContainer.class, first);
      View2dContainer container = (View2dContainer) first;
      assertEquals(2, container.getLayoutCount());
      ViewerPlugin<?> same = opening.open(dx("DX", "1", "2.25.dx.lat"));
      assertSame(first, same);
      assertNotEquals(
          seriesUid(container.getLayoutViews().get(0).getSeries()),
          seriesUid(container.getLayoutViews().get(1).getSeries()));
    } finally {
      close(core, factory);
    }
  }

  @Test
  void chestThenKneeDxOnePluginDifferentSeriesNotClonesOrExtraTabs() {
    UICore core = new UICore();
    JFrame win = new JFrame();
    core.setApplicationWindow(win);
    View2dFactory factory = new View2dFactory();
    core.registerSeriesViewerFactory(factory);
    DicomModel model = new DicomModel();
    try {
      model.addInstance(dx("CHEST", "P-CHEST", "2.25.chest"));
      new PluginOpeningStrategy(core).openIfWindow(model);
      model.addInstance(dx("KNEE", "P-KNEE", "2.25.knee"));
      new PluginOpeningStrategy(core).openIfWindow(model);
      assertEquals(1, core.getOpenViewerPlugins().size());
      View2dContainer container = (View2dContainer) core.getSelectedViewerPlugin();
      assertEquals(2, container.getLayoutCount());
      assertEquals("2.25.chest", seriesUid(container.getLayoutViews().get(0).getSeries()));
      assertEquals("2.25.knee", seriesUid(container.getLayoutViews().get(1).getSeries()));
    } finally {
      close(core, factory);
      core.setApplicationWindow(null);
      win.dispose();
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
      close(core, factory);
    }
  }

  static void close(UICore core, View2dFactory factory) {
    for (ViewerPlugin<?> plugin : List.copyOf(core.getOpenViewerPlugins())) {
      core.closeViewerPlugin(plugin);
    }
    core.unregisterSeriesViewerFactory(factory);
  }

  static String seriesUid(MediaSeries<?> series) {
    Object v = series == null ? null : series.getTagValue(TagW.SeriesInstanceUID);
    return v == null ? "" : v.toString();
  }

  static ImportedInstance dx(String name, String id, String seriesUid) {
    return new ImportedInstance(
        "SYNTHETIC^" + name,
        "SYN-" + id,
        "2.25." + id + ".study",
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
