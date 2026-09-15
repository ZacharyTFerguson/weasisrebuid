/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.print;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;
import org.weasis.dicom.explorer.pref.node.DicomPrintNode;

class DicomPrintTest {

  @Test
  void filmSessionFromOptions() {
    DicomPrintOptions options = new DicomPrintOptions();
    options.setCopies(2);
    options.setFilmSize(DicomPrintOptions.FilmSize.SIZE_10INX12IN);
    DicomPrint print =
        new DicomPrint(new DefaultDicomNode("printer", "PRINT_SCP", "127.0.0.1", 104));
    print.setOptions(options);
    assertEquals(2, print.buildFilmSession().getInt(Tag.NumberOfCopies, 0));
    assertEquals("PRINT_SCP", print.buildFilmSession().getString(Tag.RetrieveAETitle));
  }

  @Test
  void dialogOptionPaneBuildsFilmSession() {
    DefaultDicomNode printer = new DefaultDicomNode("printer", "PRINT_SCP", "127.0.0.1", 104);
    DicomPrintDialog dialog = DicomPrintDialog.open(null, printer);
    assertEquals("DICOM Print", dialog.getTitle());
    DicomPrintOptions options = new DicomPrintOptions();
    options.setCopies(3);
    options.setFilmSize(DicomPrintOptions.FilmSize.SIZE_14INX17IN);
    dialog.getOptionPane().setOptions(options);
    assertEquals(3, dialog.getPrint().buildFilmSession().getInt(Tag.NumberOfCopies, 0));
    dialog.getOptionPane().resetToDefaultValues();
    assertEquals(1, dialog.getOptionPane().getOptions().copies());
  }

  @Test
  void nCreateFilmBoxAndNActionPrint() {
    DicomPrintOptions options = new DicomPrintOptions();
    options.setCopies(2);
    options.setFilmSize(DicomPrintOptions.FilmSize.SIZE_10INX12IN);
    options.setOrientation(DicomPrintOptions.FilmOrientation.LANDSCAPE);
    DicomPrintNode node = new DicomPrintNode("printer", "PRINT_SCP", "127.0.0.1", 104, false);
    DicomPrint print = new DicomPrint(node);
    print.setOptions(options);
    Attributes session = print.nCreateFilmSession();
    assertEquals(2, session.getInt(Tag.NumberOfCopies, 0));
    assertEquals("MED", session.getString(Tag.PrintPriority));
    assertEquals("BLUE FILM", session.getString(Tag.MediumType));
    assertNull(session.getString(Tag.FilmSizeID));
    Attributes box = print.nCreateFilmBox("2.25.session");
    assertEquals("10INX12IN", box.getString(Tag.FilmSizeID));
    assertEquals("LANDSCAPE", box.getString(Tag.FilmOrientation));
    assertEquals("STANDARD\\1,1", box.getString(Tag.ImageDisplayFormat));
    Attributes ref = box.getNestedDataset(Tag.ReferencedFilmSessionSequence);
    assertEquals(UID.BasicFilmSession, ref.getString(Tag.ReferencedSOPClassUID));
    assertEquals("2.25.session", ref.getString(Tag.ReferencedSOPInstanceUID));
    Attributes image = print.nCreateImageBox(1);
    assertEquals(1, image.getInt(Tag.ImagePosition, 0));
    assertEquals("NORMAL", image.getString(Tag.Polarity));
    DicomPrint.NAction action = print.nActionPrintFilmSession("2.25.session");
    assertEquals(DicomPrint.ACTION_PRINT, action.actionTypeId());
    assertEquals(UID.BasicFilmSession, action.sopClassUid());
    assertEquals(UID.BasicGrayscalePrintManagementMeta, print.printManagementMetaSopClass());
    assertEquals(UID.BasicGrayscaleImageBox, print.imageBoxSopClass());
    DicomPrint color = new DicomPrint(new DicomPrintNode("c", "C", "127.0.0.1", 104, true));
    assertEquals(UID.BasicColorPrintManagementMeta, color.printManagementMetaSopClass());
    assertEquals(UID.BasicColorImageBox, color.imageBoxSopClass());
  }
}
