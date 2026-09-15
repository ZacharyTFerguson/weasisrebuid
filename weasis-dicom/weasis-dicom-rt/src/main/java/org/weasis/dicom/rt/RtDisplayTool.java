/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import java.awt.BorderLayout;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomElement;

public class RtDisplayTool extends ViewerPlugin<MediaElement> {

  private final StructRegionTree tree = new StructRegionTree();
  private final RtSet rtSet = new RtSet();

  public RtDisplayTool() {
    super("DICOM RT Tools");
    add(tree, BorderLayout.CENTER);
  }

  public StructRegionTree tree() {
    return tree;
  }

  public RtSet rtSet() {
    return rtSet;
  }

  public StructureSet structureSet() {
    return rtSet.structureSet();
  }

  public void display(Attributes dataset) {
    if (dataset == null) {
      return;
    }
    String modality = dataset.getString(Tag.Modality, "");
    if ("RTPLAN".equalsIgnoreCase(modality)) {
      rtSet.setPlan(Plan.from(dataset));
      return;
    }
    if ("RTDOSE".equalsIgnoreCase(modality)) {
      rtSet.setDose(Dose.from(dataset));
      return;
    }
    StructureSet set = StructureSet.from(dataset);
    rtSet.setStructureSet(set);
    tree.setStructureSet(set);
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null) {
      return;
    }
    for (MediaElement media : sequence.getMedias()) {
      if (media instanceof DicomElement dicom) {
        display(dicom.getDicomObject());
      }
    }
  }
}
