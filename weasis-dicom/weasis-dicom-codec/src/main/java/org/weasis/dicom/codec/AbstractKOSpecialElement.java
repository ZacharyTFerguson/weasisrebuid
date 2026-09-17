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

import java.util.ArrayList;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.service.UICore;

/**
 * Key Object Selection document. SOP Instance UID root is {@code weasis.dicom.root.uid} (default
 * 2.25).
 */
public abstract class AbstractKOSpecialElement extends DicomSpecialElement {

  protected AbstractKOSpecialElement(DcmMediaReader mediaIO) {
    super(mediaIO);
    setMimeType(DicomMime.KO_DICOM);
  }

  public static String rootUid() {
    String root = System.getProperty("weasis.dicom.root.uid");
    if (root == null || root.isBlank()) {
      root =
          UICore.getInstance().getSystemPreferences().getProperty("weasis.dicom.root.uid", "2.25");
    }
    return root == null || root.isBlank() ? "2.25" : root;
  }

  public List<String> getReferencedSopInstanceUIDList() {
    return new ArrayList<>(getReferencedSopInstanceUIDs());
  }

  public String getDocumentTitle() {
    Attributes dcm = getDicomObject();
    if (dcm == null) {
      return "KO";
    }
    String title = dcm.getString(Tag.ContentDescription);
    return title == null || title.isBlank() ? defaultTitle(dcm) : title;
  }

  String defaultTitle(Attributes dcm) {
    String meaning = conceptMeaning(dcm);
    return meaning.isBlank() ? "Key Object Selection" : meaning;
  }

  public boolean isRejectionNote() {
    return false;
  }

  static String conceptMeaning(Attributes dcm) {
    Sequence seq = dcm == null ? null : dcm.getSequence(Tag.ConceptNameCodeSequence);
    if (seq == null || seq.isEmpty()) {
      return "";
    }
    return seq.get(0).getString(Tag.CodeMeaning, "");
  }

  static String conceptCode(Attributes dcm) {
    Sequence seq = dcm == null ? null : dcm.getSequence(Tag.ConceptNameCodeSequence);
    if (seq == null || seq.isEmpty()) {
      return "";
    }
    return seq.get(0).getString(Tag.CodeValue, "");
  }
}
