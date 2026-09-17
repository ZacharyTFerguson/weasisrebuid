/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.sr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/**
 * IMAGE content item: Referenced SOP Instance UID (and optional SOP Class / frames) from Referenced
 * SOP Sequence.
 */
public class SRImageReference {

  private final String sopClassUid;
  private final String sopInstanceUid;
  private final int[] frames;

  public SRImageReference(String sopClassUid, String sopInstanceUid, int[] frames) {
    this.sopClassUid = sopClassUid == null ? "" : sopClassUid;
    this.sopInstanceUid = sopInstanceUid == null ? "" : sopInstanceUid;
    this.frames = frames == null ? new int[0] : frames.clone();
  }

  public String sopClassUid() {
    return sopClassUid;
  }

  public String sopInstanceUid() {
    return sopInstanceUid;
  }

  public int[] frames() {
    return frames.clone();
  }

  public boolean isPresent() {
    return !sopInstanceUid.isBlank();
  }

  public static SRImageReference fromContentItem(Attributes item) {
    if (item == null || !"IMAGE".equals(item.getString(Tag.ValueType, ""))) {
      return null;
    }
    Sequence refs = item.getSequence(Tag.ReferencedSOPSequence);
    if (refs != null && !refs.isEmpty()) {
      Attributes ref = refs.get(0);
      String sopClass = ref.getString(Tag.ReferencedSOPClassUID, "");
      String sopInst = ref.getString(Tag.ReferencedSOPInstanceUID, "");
      int[] frames = ref.getInts(Tag.ReferencedFrameNumber);
      return new SRImageReference(sopClass, sopInst, frames);
    }
    String sopInst = item.getString(Tag.ReferencedSOPInstanceUID, "");
    if (sopInst == null || sopInst.isBlank()) {
      return null;
    }
    return new SRImageReference("", sopInst, new int[0]);
  }

  public static List<SRImageReference> fromDocument(Attributes dataset) {
    List<SRImageReference> out = new ArrayList<>();
    if (dataset != null) {
      collect(dataset.getSequence(Tag.ContentSequence), out);
    }
    return Collections.unmodifiableList(out);
  }

  static void collect(Sequence sequence, List<SRImageReference> out) {
    if (sequence == null) {
      return;
    }
    for (Attributes item : sequence) {
      SRImageReference ref = fromContentItem(item);
      if (ref != null && ref.isPresent()) {
        out.add(ref);
      }
      collect(item.getSequence(Tag.ContentSequence), out);
    }
  }

  @Override
  public String toString() {
    return sopInstanceUid + (frames.length == 0 ? "" : Arrays.toString(frames));
  }
}
