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
import java.util.Collections;
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

/**
 * Key images and Key Object Selection documents (docs: Build DICOM KO and PR). Root UID from {@code
 * weasis.dicom.root.uid} default 2.25.
 */
public class KOManager {

  public static final String ROOT_UID = "2.25";

  private final LinkedHashSet<String> keySopInstanceUids = new LinkedHashSet<>();

  public boolean toggleKeyImage(String sopInstanceUid) {
    if (sopInstanceUid == null || sopInstanceUid.isBlank()) {
      return false;
    }
    if (!keySopInstanceUids.add(sopInstanceUid)) {
      keySopInstanceUids.remove(sopInstanceUid);
      return false;
    }
    return true;
  }

  public boolean isKeyImage(String sopInstanceUid) {
    return keySopInstanceUids.contains(sopInstanceUid);
  }

  public Set<String> keyImages() {
    return Collections.unmodifiableSet(keySopInstanceUids);
  }

  public Attributes buildKoDocument(Attributes sourceImage) {
    Attributes src = sourceImage == null ? new Attributes() : sourceImage;
    String sop = UIDUtils.createUID(ROOT_UID);
    Attributes ko = new Attributes();
    ko.setString(Tag.SOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    ko.setString(Tag.SOPInstanceUID, VR.UI, sop);
    ko.setString(
        Tag.StudyInstanceUID,
        VR.UI,
        src.getString(Tag.StudyInstanceUID, UIDUtils.createUID(ROOT_UID)));
    ko.setString(Tag.SeriesInstanceUID, VR.UI, UIDUtils.createUID(ROOT_UID));
    ko.setString(Tag.Modality, VR.CS, "KO");
    ko.setString(Tag.Manufacturer, VR.LO, "Weasis");
    ko.setString(Tag.ValueType, VR.CS, "CONTAINER");
    ko.setString(Tag.ContinuityOfContent, VR.CS, "SEPARATE");
    Sequence evidence = ko.newSequence(Tag.CurrentRequestedProcedureEvidenceSequence, 1);
    Attributes ev = new Attributes();
    ev.setString(Tag.StudyInstanceUID, VR.UI, ko.getString(Tag.StudyInstanceUID));
    Sequence rs = ev.newSequence(Tag.ReferencedSeriesSequence, 1);
    Attributes series = new Attributes();
    series.setString(
        Tag.SeriesInstanceUID,
        VR.UI,
        src.getString(Tag.SeriesInstanceUID, UIDUtils.createUID(ROOT_UID)));
    Sequence sopSeq = series.newSequence(Tag.ReferencedSOPSequence, keySopInstanceUids.size());
    List<String> keys = new ArrayList<>(keySopInstanceUids);
    if (keys.isEmpty()) {
      String one = src.getString(Tag.SOPInstanceUID);
      if (one != null) {
        keys.add(one);
      }
    }
    for (String uid : keys) {
      Attributes ref = new Attributes();
      ref.setString(
          Tag.ReferencedSOPClassUID, VR.UI, src.getString(Tag.SOPClassUID, UID.CTImageStorage));
      ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, uid);
      sopSeq.add(ref);
    }
    rs.add(series);
    evidence.add(ev);

    Sequence content = ko.newSequence(Tag.ContentSequence, keys.size());
    for (String uid : keys) {
      Attributes item = new Attributes();
      item.setString(Tag.RelationshipType, VR.CS, "CONTAINS");
      item.setString(Tag.ValueType, VR.CS, "IMAGE");
      Sequence img = item.newSequence(Tag.ReferencedSOPSequence, 1);
      Attributes ref = new Attributes();
      ref.setString(
          Tag.ReferencedSOPClassUID, VR.UI, src.getString(Tag.SOPClassUID, UID.CTImageStorage));
      ref.setString(Tag.ReferencedSOPInstanceUID, VR.UI, uid);
      img.add(ref);
      content.add(item);
    }
    return ko;
  }

  public File writeKo(File dest, Attributes sourceImage) throws IOException {
    Attributes ko = buildKoDocument(sourceImage);
    Attributes fmi = new Attributes();
    fmi.setBytes(Tag.FileMetaInformationVersion, VR.OB, new byte[] {0, 1});
    fmi.setString(Tag.MediaStorageSOPClassUID, VR.UI, UID.KeyObjectSelectionDocumentStorage);
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, ko.getString(Tag.SOPInstanceUID));
    fmi.setString(Tag.TransferSyntaxUID, VR.UI, UID.ExplicitVRLittleEndian);
    fmi.setString(Tag.ImplementationClassUID, VR.UI, "2.25.1918");
    fmi.setString(Tag.ImplementationVersionName, VR.SH, "WEASISREBUILD");
    try (DicomOutputStream out = new DicomOutputStream(dest)) {
      out.writeDataset(fmi, ko);
    }
    return dest;
  }
}
