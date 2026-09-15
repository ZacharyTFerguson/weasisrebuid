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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.util.List;
import org.dcm4che3.data.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.ui.editor.image.HistogramView;

class KeyObjectToolBarHaveTest {

  @Test
  void starAndFilterButtonsToggleKeyImages() {
    KeyObjectToolBar bar = new KeyObjectToolBar();
    assertEquals("Key Object", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertEquals(2, bar.getComponent().getComponentCount());
    assertTrue(bar.toggle("1.2.3"));
    assertTrue(bar.getManager().isKeyImage("1.2.3"));
    assertTrue(bar.filter());
    assertEquals(List.of("1.2.3"), bar.getManager().visibleSops(List.of("1.2.3", "9.9")));
    assertFalse(bar.filter());
    assertEquals(2, bar.getManager().visibleSops(List.of("1.2.3", "9.9")).size());
  }

  @Test
  void shortcutKTogglesBoundViewSop(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2d view = new View2d();
    view.load(file.toFile());
    String sop = view.getDataset().getString(Tag.SOPInstanceUID);
    KeyObjectToolBar bar = new KeyObjectToolBar();
    bar.bind(view);
    assertTrue(bar.star());
    assertTrue(view.getKoManager().isKeyImage(sop));
    view.getEventManager()
        .keyPressed(new KeyEvent(view, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_K, 'k'));
    assertFalse(view.getKoManager().isKeyImage(sop));
    assertEquals(ActionW.KO, ActionW.getAction("ko"));
  }

  @Test
  void histogramDockFollowsWindowLevelOutput(@TempDir Path dir) throws Exception {
    Path file = dir.resolve("ct.dcm");
    TestCt.write(file.toFile(), 8, 40, 400);
    View2d view = new View2d();
    view.load(file.toFile());
    HistogramView dock = new HistogramView();
    dock.bind(view);
    double meanAt400 = dock.getPanel().getData().getMean();
    view.setWindowLevel(1, 40);
    dock.refresh();
    assertEquals(64, dock.getPanel().getData().getSamples());
    assertNotEquals(meanAt400, dock.getPanel().getData().getMean(), 1.0);
  }
}
