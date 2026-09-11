/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleTaggable implements Taggable {

  private final Map<TagW, Object> tags = new ConcurrentHashMap<>();

  @Override
  public void setTag(TagW tag, Object value) {
    if (tag == null) {
      return;
    }
    if (value == null) {
      tags.remove(tag);
    } else {
      tags.put(tag, value);
    }
  }

  @Override
  public void setTagNoNull(TagW tag, Object value) {
    if (value != null) {
      setTag(tag, value);
    }
  }

  @Override
  public boolean containTagKey(TagW tag) {
    return tags.containsKey(tag);
  }

  @Override
  public Object getTagValue(TagW tag) {
    return tags.get(tag);
  }
}
