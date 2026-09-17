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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.gui.Insertable;

class DcmHeaderToolBarHaveTest {

  @Test
  void dumpsSelectedImageDatasetTags() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.CTImageStorage);
    dcm.setString(Tag.SOPInstanceUID, VR.UI, "1.2.3.4.5");
    dcm.setString(Tag.PatientID, VR.LO, "P-42");
    dcm.setString(Tag.Modality, VR.CS, "CT");
    dcm.setInt(Tag.Rows, VR.US, 2);
    dcm.setInt(Tag.Columns, VR.US, 2);
    dcm.setString(Tag.PhotometricInterpretation, VR.CS, "MONOCHROME2");
    dcm.setInt(Tag.PixelData, VR.OW, 1, 2, 3, 4);

    DcmHeaderToolBar bar = new DcmHeaderToolBar();
    assertEquals("DICOM Header", bar.getComponentName());
    assertEquals(Insertable.Type.TOOLBAR, bar.getType());
    String dump = bar.dump(dcm);
    assertTrue(dump.contains("SOPInstanceUID"));
    assertTrue(dump.contains("1.2.3.4.5"));
    assertTrue(dump.contains("PatientID"));
    assertTrue(dump.contains("P-42"));
    assertTrue(dump.contains("(0010,0020)") || dump.contains("0010,0020"));

    View2d view = new View2d();
    view.load(dcm);
    bar.bind(view);
    String selected = bar.dumpSelected();
    assertTrue(selected.contains("1.2.3.4.5"));
    assertEquals(selected, bar.lastDump());
    assertEquals(selected, bar.dumpArea().getText());
    assertEquals(DcmHeaderToolBar.DUMP, bar.dumpButton().getName());
    assertEquals(DcmHeaderToolBar.DUMP_TEXT, bar.dumpArea().getName());
    assertTrue(selected.contains("[OW]"));
  }
}
