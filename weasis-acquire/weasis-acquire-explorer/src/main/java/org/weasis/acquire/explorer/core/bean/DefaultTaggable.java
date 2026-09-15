/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.core.bean;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.weasis.acquire.explorer.util.AbstractBean;
import org.weasis.core.api.media.data.TagW;
import org.weasis.core.api.media.data.Taggable;

/** Mutable {@link Taggable} that notifies listeners when a tag changes. */
public class DefaultTaggable extends AbstractBean<TagW> implements Taggable {

  private final Map<TagW, Object> tags = new LinkedHashMap<>();

  @Override
  public void setTag(TagW tag, Object value) {
    if (tag == null) {
      return;
    }
    Object old;
    if (value == null) {
      old = tags.remove(tag);
    } else {
      old = tags.put(tag, value);
    }
    firePropertyChange(tag, old, value);
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

  public Map<TagW, Object> getTagEntrySet() {
    return Collections.unmodifiableMap(tags);
  }
}
