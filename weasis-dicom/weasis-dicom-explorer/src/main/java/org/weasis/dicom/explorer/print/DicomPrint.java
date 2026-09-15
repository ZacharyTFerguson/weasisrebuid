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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;

/** DICOM Print SCP N-ACTION film session builder (headless dataset only). */
public final class DicomPrint {

  private final DefaultDicomNode printer;
  private DicomPrintOptions options = new DicomPrintOptions();

  public DicomPrint(DefaultDicomNode printer) {
    this.printer = printer;
  }

  public void setOptions(DicomPrintOptions options) {
    this.options = options == null ? new DicomPrintOptions() : options;
  }

  public Attributes buildFilmSession() {
    Attributes session = options.toFilmSessionAttributes();
    if (printer != null) {
      session.setString(Tag.RetrieveAETitle, VR.AE, printer.aeTitle());
    }
    return session;
  }
}
