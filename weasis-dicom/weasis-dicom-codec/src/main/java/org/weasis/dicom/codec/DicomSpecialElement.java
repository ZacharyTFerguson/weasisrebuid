/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.TagW;

/** Non-image SOP (KO, PR, SR, SEG, …) bound via {@link DicomSpecialElementFactory}. */
public class DicomSpecialElement extends MediaElement
    implements DicomElement, SpecialElementReferences {

  private final DcmMediaReader mediaIO;
  private final Set<String> referenced = new LinkedHashSet<>();

  public DicomSpecialElement(DcmMediaReader mediaIO) {
    this.mediaIO = mediaIO;
    if (mediaIO != null) {
      setMediaURI(mediaIO.getUri());
      Attributes dcm = mediaIO.getDicomObject();
      if (dcm != null) {
        setMimeType(DicomMime.fromSopClass(dcm.getString(Tag.SOPClassUID, "")));
        setTag(TagW.SOPInstanceUID, dcm.getString(Tag.SOPInstanceUID, ""));
        setTag(TagW.SOPClassUID, dcm.getString(Tag.SOPClassUID, ""));
        setTag(TagW.Modality, dcm.getString(Tag.Modality, ""));
        setTag(TagW.SeriesInstanceUID, dcm.getString(Tag.SeriesInstanceUID, ""));
        collectReferences(dcm, referenced);
      }
    }
  }

  static void collectReferences(Attributes dcm, Set<String> dest) {
    if (dcm == null) {
      return;
    }
    walk(dcm.getSequence(Tag.CurrentRequestedProcedureEvidenceSequence), dest);
    walk(dcm.getSequence(Tag.ReferencedSeriesSequence), dest);
    String sop = dcm.getString(Tag.ReferencedSOPInstanceUID);
    if (sop != null) {
      dest.add(sop);
    }
  }

  private static void walk(Sequence seq, Set<String> dest) {
    if (seq == null) {
      return;
    }
    for (Attributes item : seq) {
      String sop = item.getString(Tag.ReferencedSOPInstanceUID);
      if (sop != null) {
        dest.add(sop);
      }
      walk(item.getSequence(Tag.ReferencedSeriesSequence), dest);
      walk(item.getSequence(Tag.ReferencedSOPSequence), dest);
      walk(item.getSequence(Tag.ReferencedImageSequence), dest);
    }
  }

  @Override
  public DcmMediaReader getMediaReader() {
    return mediaIO;
  }

  @Override
  public String getKey() {
    Object v = getTagValue(TagW.SOPInstanceUID);
    return v == null ? "" : v.toString();
  }

  @Override
  public Set<String> getReferencedSopInstanceUIDs() {
    return Collections.unmodifiableSet(referenced);
  }

  @Override
  public boolean isSopInstanceReferenced(String sopInstanceUid) {
    return sopInstanceUid != null && referenced.contains(sopInstanceUid);
  }

  public String getShortLabel() {
    Object modality = getTagValue(TagW.Modality);
    return (modality == null ? "DICOM" : modality.toString()) + " " + getKey();
  }
}
