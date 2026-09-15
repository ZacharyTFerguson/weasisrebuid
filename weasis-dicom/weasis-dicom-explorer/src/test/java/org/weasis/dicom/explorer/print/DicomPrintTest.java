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

import org.dcm4che3.data.Tag;
import org.junit.jupiter.api.Test;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;

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
}
