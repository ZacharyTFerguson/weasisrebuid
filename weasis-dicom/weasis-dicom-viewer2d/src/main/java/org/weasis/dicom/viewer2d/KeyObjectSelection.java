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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.io.DicomOutputStream;
import org.dcm4che3.util.UIDUtils;
import org.weasis.core.api.service.WProperties;

/**
 * Key Object Selection: star (K) marks the current image; apply filters the series to referenced
 * key images. Toolbar hidden by default ({@code weasis.all.keyobjecttoolbar.visible}). Only
 * Weasis-created KOs are deletable.
 */
public final class KeyObjectSelection {

  public static final String TOOLBAR_PREF = "weasis.all.keyobjecttoolbar.visible";
  public static final String MANUFACTURER = "Weasis rebuild";

  private final LinkedHashSet<String> starred = new LinkedHashSet<>();
  private boolean createdHere = true;

  public void star(String sopInstanceUid) {
    if (sopInstanceUid != null && !sopInstanceUid.isBlank()) {
      starred.add(sopInstanceUid);
    }
  }

  public void unstar(String sopInstanceUid) {
    starred.remove(sopInstanceUid);
  }

  public boolean isStarred(String sopInstanceUid) {
    return starred.contains(sopInstanceUid);
  }

  public Set<String> starred() {
    return Set.copyOf(starred);
  }

  /** Filter a series SOP list to key images (apply KOS). */
  public List<String> filter(List<String> seriesSopUids) {
    if (seriesSopUids == null) {
      return List.of();
    }
    List<String> out = new ArrayList<>();
    for (String sop : seriesSopUids) {
      if (starred.contains(sop)) {
        out.add(sop);
      }
    }
    return out;
  }

  public boolean canDelete() {
    return createdHere;
  }

  public static boolean canDelete(Attributes ko) {
    return ko != null && MANUFACTURER.equals(ko.getString(Tag.Manufacturer));
  }

  public static boolean toolbarVisible(WProperties prefs) {
    if (prefs == null) {
      return false;
    }
    return prefs.getBooleanProperty(TOOLBAR_PREF, false);
  }

  public Attributes exportDocument(Attributes sourceImage) {
    Attributes ko = new Attributes();
    String sop = UIDUtils.createUID("2.25");
    ko.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    ko.setString(Tag.SOPInstanceUID, VR.UI, sop);
    ko.setString(Tag.Modality, VR.CS, "KO");
    ko.setString(Tag.Manufacturer, VR.LO, MANUFACTURER);
    ko.setString(Tag.ValueType, VR.CS, "CONTAINER");
    ko.setString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");
    ko.setString(Tag.CompletionFlag, VR.CS, "COMPLETE");
    ko.setString(Tag.VerificationFlag, VR.CS, "UNVERIFIED");
    Sequence cn = ko.newSequence(Tag.ConceptNameCodeSequence, 1);
    Attributes code = new Attributes();
    code.setString(Tag.CodeValue, VR.SH, "113000");
    code.setString(Tag.CodingSchemeDesignator, VR.SH, "DCM");
    code.setString(Tag.CodeMeaning, VR.LO, "Of Interest");
    cn.add(code);
    if (sourceImage != null) {
      ko.setString(Tag.PatientName, VR.PN, sourceImage.getString(Tag.PatientName, "SYNTHETIC"));
      ko.setString(Tag.PatientID, VR.LO, sourceImage.getString(Tag.PatientID, "SYN"));
      ko.setString(Tag.StudyInstanceUID, VR.UI, sourceImage.getString(Tag.StudyInstanceUID));
      Sequence ev = ko.newSequence(Tag.CurrentRequestedProcedureEvidenceSequence, 1);
      Attributes study = new Attributes();
      study.setString(Tag.StudyInstanceUID, VR.UI, sourceImage.getString(Tag.StudyInstanceUID));
      Sequence series = study.newSequence(Tag.ReferencedSeriesSequence, 1);
      Attributes ser = new Attributes();
      ser.setString(Tag.SeriesInstanceUID, VR.UI, sourceImage.getString(Tag.SeriesInstanceUID));
      Sequence refs = ser.newSequence(Tag.ReferencedSOPSequence, starred.size());
      String sopClass = sourceImage.getString(Tag.SOPClassUID, UID.CTImageStorage);
      for (String uid : starred) {
        Attributes r = new Attributes();
        r.setString(Tag.ReferencedSOPClassUID, VR.UI, sopClass);
        r.setString(Tag.ReferencedSOPInstanceUID, VR.UI, uid);
        refs.add(r);
      }
      series.add(ser);
      ev.add(study);
    }
    return ko;
  }

  public void exportFile(File dest, Attributes sourceImage) throws IOException {
    Attributes ko = exportDocument(sourceImage);
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, ko.getString(Tag.SOPClassUID));
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, ko.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, ko);
    }
  }

  public static KeyObjectSelection fromDocument(Attributes ko) {
    KeyObjectSelection sel = new KeyObjectSelection();
    sel.createdHere = canDelete(ko);
    if (ko == null) {
      return sel;
    }
    Sequence ev = ko.getSequence(Tag.CurrentRequestedProcedureEvidenceSequence);
    if (ev == null) {
      return sel;
    }
    for (Attributes study : ev) {
      Sequence series = study.getSequence(Tag.ReferencedSeriesSequence);
      if (series == null) {
        continue;
      }
      for (Attributes ser : series) {
        Sequence refs = ser.getSequence(Tag.ReferencedSOPSequence);
        if (refs == null) {
          continue;
        }
        for (Attributes r : refs) {
          sel.star(r.getString(Tag.ReferencedSOPInstanceUID));
        }
      }
    }
    return sel;
  }
}
