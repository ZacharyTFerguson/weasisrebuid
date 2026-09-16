/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.au;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;

class AuChromeHaveTest {

  @Test
  void containerWiresPlayPauseStopChrome() {
    AuContainer container = new AuContainer();
    assertEquals(AuContainer.NAME, container.getPluginName());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> AuToolBar.NAME.equals(b.getComponentName())));
    AuToolBar bar = container.getToolBar();
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertSame(container.getAuView(), bar.getView());
    AbstractButton play = (AbstractButton) bar.getComponent(0);
    AbstractButton pause = (AbstractButton) bar.getComponent(1);
    AbstractButton stop = (AbstractButton) bar.getComponent(2);
    assertEquals("Play", play.getText());
    assertEquals("play", play.getName());
    assertEquals("Pause", pause.getText());
    assertEquals("pause", pause.getName());
    assertEquals("Stop", stop.getText());
    assertEquals("stop", stop.getName());

    container
        .getAuView()
        .display(AuViewTest.voiceUb(8000.0, new byte[] {(byte) 128, (byte) 255, 0, (byte) 128}));
    play.doClick();
    assertTrue(container.getAuView().isPlaying());
    container.getAuView().seek(2);
    pause.doClick();
    assertFalse(container.getAuView().isPlaying());
    assertEquals(2, container.getAuView().position());
    stop.doClick();
    assertFalse(container.getAuView().isPlaying());
    assertEquals(0, container.getAuView().position());
  }
}
