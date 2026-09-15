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

import java.util.Comparator;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.TagW;

/** Series sort: SR/DOC last (4.7.3). Instance number otherwise. */
public final class SortSeriesStack {
  private SortSeriesStack() {}

  public static Comparator<MediaElement> instanceNumber() {
    return Comparator.comparingInt(e -> intTag(e, TagW.InstanceNumber));
  }

  public static Comparator<MediaElement> sopClassDocumentsLast() {
    return (a, b) -> {
      int da = isDocument(a) ? 1 : 0;
      int db = isDocument(b) ? 1 : 0;
      if (da != db) {
        return Integer.compare(da, db);
      }
      return instanceNumber().compare(a, b);
    };
  }

  static boolean isDocument(MediaElement e) {
    if (e == null) {
      return false;
    }
    String mime = e.getMimeType();
    if (mime != null
        && (mime.startsWith("sr/")
            || mime.startsWith("encap/")
            || DicomMime.SR_DICOM.equals(mime)
            || DicomMime.ENCAP_DICOM.equals(mime))) {
      return true;
    }
    Object sop = e.getTagValue(TagW.SOPClassUID);
    String uid = sop == null ? "" : sop.toString();
    return uid.contains("1.2.840.10008.10.0.2.2.1.88") || uid.contains("1.2.840.10008.10.0.2.2.1.104");
  }

  static int intTag(MediaElement e, TagW tag) {
    Object v = e == null ? null : e.getTagValue(tag);
    if (v instanceof Number n) {
      return n.intValue();
    }
    if (v instanceof String s) {
      try {
        return Integer.parseInt(s.trim());
      } catch (NumberFormatException ex) {
        return 0;
      }
    }
    return 0;
  }
}
