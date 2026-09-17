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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** Helper for DICOM SQ items (KO Current Requested Procedure Evidence). */
public final class TagSeq {
  private TagSeq() {}

  public static Sequence get(Attributes dcm, int tag) {
    return dcm == null ? null : dcm.getSequence(tag);
  }

  public static Attributes firstItem(Attributes dcm, int tag) {
    Sequence seq = get(dcm, tag);
    if (seq == null || seq.isEmpty()) {
      return null;
    }
    return seq.get(0);
  }

  public static Sequence referencedSeries(Attributes dcm) {
    return get(dcm, Tag.CurrentRequestedProcedureEvidenceSequence);
  }
}
