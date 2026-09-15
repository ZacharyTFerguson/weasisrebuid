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

public class TagView {
  private final TagW[] tags;
  private final String format;

  public TagView(TagW... tags) {
    this(null, tags);
  }

  public TagView(String format, TagW... tags) {
    this.format = format;
    this.tags = tags == null ? new TagW[0] : tags;
  }

  public TagW[] getTags() {
    return tags;
  }

  public String getFormat() {
    return format;
  }

  public String getFormattedText(TagReadable readable) {
    if (readable == null || tags.length == 0) {
      return "";
    }
    Object v = readable.getTagValue(tags[0]);
    return v == null ? "" : v.toString();
  }
}
