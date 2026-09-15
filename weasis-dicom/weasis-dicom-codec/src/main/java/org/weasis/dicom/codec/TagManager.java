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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.dcm4che3.data.Attributes;
import org.weasis.core.api.media.data.TagW;
import org.weasis.core.api.media.data.Taggable;

/** Copies a configured set of DICOM tags onto a {@link Taggable}. */
public class TagManager {
  private final Map<Integer, TagD> tags = new LinkedHashMap<>();

  public TagManager add(TagD tag) {
    if (tag != null) {
      tags.put(tag.getId(), tag);
    }
    return this;
  }

  public TagManager add(int tag) {
    return add(TagD.get(tag));
  }

  public Collection<TagD> getTags() {
    return tags.values();
  }

  public void readTags(Attributes dcm, Taggable dest) {
    if (dcm == null || dest == null) {
      return;
    }
    for (TagD tag : tags.values()) {
      Object value = dcm.getValue(tag.getId());
      if (value != null) {
        dest.setTag(tag, dcm.getString(tag.getId(), null));
      }
    }
  }

  public Object getTagValue(Taggable src, TagW tag) {
    return src == null || tag == null ? null : src.getTagValue(tag);
  }
}
