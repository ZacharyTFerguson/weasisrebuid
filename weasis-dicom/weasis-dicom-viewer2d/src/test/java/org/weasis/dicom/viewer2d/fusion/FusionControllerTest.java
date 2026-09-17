/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.fusion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.AbstractButton;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.viewer2d.View2d;
import org.weasis.dicom.viewer2d.View2dContainer;

class FusionControllerTest {

  @Test
  void targetViewsCollectOverlayDestinations() {
    FusionController fusion = new FusionController();
    View2d ct = new View2d();
    fusion.addTarget(ct);
    assertEquals(1, fusion.getTargetViews().size());
    assertTrue(fusion.getTargetViews().contains(ct));
    fusion.setOverlayOpacity(0.5);
    assertEquals(0.5, fusion.getOverlayOpacity());
    assertEquals(0.5, fusion.getState().getOpacity(), 1e-9);
  }

  @Test
  void suvBwIsStoredActivityTimesBqmlFactor() {
    FusionController fusion = new FusionController();
    Attributes dcm = new Attributes();
    dcm.setString(Tag.Units, VR.CS, "BQML");
    dcm.setDouble(Tag.PatientWeight, VR.DS, 70.0);
    Sequence seq = dcm.newSequence(Tag.RadiopharmaceuticalInformationSequence, 1);
    Attributes radio = new Attributes();
    radio.setDouble(Tag.RadionuclideTotalDose, VR.DS, 70_000_000.0);
    seq.add(radio);
    assertEquals(0.001, fusion.suvBw(dcm, 1.0), 1e-9);
    assertEquals(0.002, fusion.suvBw(dcm, 2.0), 1e-9);
    Attributes counts = new Attributes();
    counts.setString(Tag.Units, VR.CS, "CNTS");
    assertEquals(2.0, fusion.suvBw(counts, 2.0), 1e-9);
  }

  @Test
  void applyLutUpdatesStateAndColorBar() {
    FusionController fusion = new FusionController();
    assertEquals(FusionColorScale.HOT_IRON, fusion.getState().getLut());
    fusion.applyLut(FusionColorScale.PET);
    assertEquals(FusionColorScale.PET, fusion.getState().getLut());
    assertEquals(FusionColorScale.PET, fusion.getColorBar().getLut());
  }

  @Test
  void view2dContainerWiresTargetViewsAndFusionChrome() {
    View2dContainer container = new View2dContainer();
    FusionController fusion = container.getFusionController();
    assertTrue(fusion.getTargetViews().contains(container.getView2d()));
    assertEquals(FusionColorBar.NAME, container.getFusionColorBar().getComponentName());
    AbstractButton hotIron = (AbstractButton) container.getFusionColorBar().getComponent(0);
    AbstractButton pet = (AbstractButton) container.getFusionColorBar().getComponent(1);
    assertEquals(FusionColorScale.HOT_IRON, hotIron.getText());
    assertEquals(FusionColorScale.PET, pet.getText());
    assertEquals(FusionColorScale.HOT_IRON, hotIron.getName());
    assertEquals(FusionColorScale.PET, pet.getName());
    pet.doClick();
    assertEquals(FusionColorScale.PET, fusion.getState().getLut());
    assertEquals(FusionColorScale.PET, fusion.getColorBar().getLut());
    hotIron.doClick();
    assertEquals(FusionColorScale.HOT_IRON, fusion.getState().getLut());
    container.setLayoutCount(2);
    assertEquals(2, fusion.getTargetViews().size());
    container.setLayoutCount(1);
    assertEquals(1, fusion.getTargetViews().size());
    assertTrue(
        container.getSeriesViewerUI().getToolBar().stream()
            .anyMatch(b -> FusionColorBar.NAME.equals(b.getComponentName())));
  }
}
