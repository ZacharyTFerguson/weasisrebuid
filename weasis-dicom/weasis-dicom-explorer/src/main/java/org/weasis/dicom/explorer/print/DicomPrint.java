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
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.weasis.dicom.explorer.pref.node.DefaultDicomNode;
import org.weasis.dicom.explorer.pref.node.DicomPrintNode;

/**
 * DICOM Print Management datasets (PS3.4 Annex H). Headless Have: N-CREATE Film Session / Film Box
 * / Image Box and N-ACTION Print (Action Type ID 1). No live SCP.
 */
public final class DicomPrint {

  /** Print Management N-ACTION Print. */
  public static final int ACTION_PRINT = 1;

  public record NAction(String sopClassUid, String sopInstanceUid, int actionTypeId) {}

  private final DefaultDicomNode printer;
  private DicomPrintOptions options = new DicomPrintOptions();

  public DicomPrint(DefaultDicomNode printer) {
    this.printer = printer;
  }

  public DicomPrint(DicomPrintNode node) {
    this(node == null ? null : node.asNode());
    if (node != null) {
      options.setColor(node.color());
    }
  }

  public void setOptions(DicomPrintOptions options) {
    this.options = options == null ? new DicomPrintOptions() : options;
  }

  public DicomPrintOptions options() {
    return options;
  }

  public Attributes buildFilmSession() {
    return nCreateFilmSession();
  }

  public Attributes nCreateFilmSession() {
    Attributes session = options.toFilmSessionAttributes();
    if (printer != null) {
      session.setString(Tag.RetrieveAETitle, VR.AE, printer.aeTitle());
    }
    return session;
  }

  public Attributes nCreateFilmBox(String filmSessionSopUid) {
    Attributes box = new Attributes();
    box.setString(Tag.ImageDisplayFormat, VR.ST, options.imageDisplayFormat());
    box.setString(Tag.FilmOrientation, VR.CS, options.orientation().name());
    box.setString(Tag.FilmSizeID, VR.CS, options.filmSize().dicomId());
    box.setString(Tag.MagnificationType, VR.CS, options.magnificationType());
    if (filmSessionSopUid != null && !filmSessionSopUid.isBlank()) {
      Attributes ref = new Attributes();
      ref.setString(Tag.ReferencedSOPClassUID, VR.UI, UID.BasicFilmSession);
      ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, filmSessionSopUid);
      box.newSequence(Tag.ReferencedFilmSessionSequence, 1).add(ref);
    }
    return box;
  }

  public Attributes nCreateImageBox(int imagePosition) {
    Attributes box = new Attributes();
    box.setInt(Tag.ImagePosition, VR.US, Math.max(1, imagePosition));
    box.setString(Tag.Polarity, VR.CS, options.polarity());
    return box;
  }

  public NAction nActionPrintFilmSession(String filmSessionSopUid) {
    return new NAction(UID.BasicFilmSession, filmSessionSopUid, ACTION_PRINT);
  }

  public NAction nActionPrintFilmBox(String filmBoxSopUid) {
    return new NAction(UID.BasicFilmBox, filmBoxSopUid, ACTION_PRINT);
  }

  public String printManagementMetaSopClass() {
    return options.color()
        ? UID.BasicColorPrintManagementMeta
        : UID.BasicGrayscalePrintManagementMeta;
  }

  public String filmSessionSopClass() {
    return UID.BasicFilmSession;
  }

  public String filmBoxSopClass() {
    return UID.BasicFilmBox;
  }

  public String imageBoxSopClass() {
    return options.color() ? UID.BasicColorImageBox : UID.BasicGrayscaleImageBox;
  }
}
