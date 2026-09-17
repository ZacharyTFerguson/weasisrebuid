/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.AbstractButton;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;

class WaveChromeHaveTest {

  @Test
  void containerWiresTwoFourTwelveChrome() {
    WaveContainer container = new WaveContainer();
    assertEquals(WaveContainer.NAME, container.getPluginName());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> WaveformToolBar.NAME.equals(b.getComponentName())));
    WaveformToolBar bar = container.getWaveformToolBar();
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    assertSame(container.getWaveView(), bar.boundView());
    AbstractButton two = (AbstractButton) bar.getComponent(0);
    AbstractButton four = (AbstractButton) bar.getComponent(1);
    AbstractButton twelve = (AbstractButton) bar.getComponent(2);
    assertEquals("2", two.getText());
    assertEquals("2", two.getName());
    assertEquals("4", four.getText());
    assertEquals("4", four.getName());
    assertEquals("12", twelve.getText());
    assertEquals("12", twelve.getName());

    container
        .getWaveView()
        .display(
            WaveViewTest.twoLeadSs(
                500.0, 0.005, Unit.MILLIVOLT, WaveViewTest.shorts(200, 400, 200, 400)));
    assertEquals(Format.TWO, container.getWaveView().format());
    twelve.doClick();
    assertEquals(Format.DEFAULT, container.getWaveView().format());
    four.doClick();
    assertEquals(Format.FOUR, container.getWaveView().format());
    two.doClick();
    assertEquals(Format.TWO, container.getWaveView().format());
  }
}
