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
import java.util.Collections;
import java.util.List;

/** SR Document Content Module: document title plus Content Sequence items. */
public class SRDocumentContentModule {

  private String title = "";
  private final List<SRDocumentContent> contents = new ArrayList<>();

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title == null ? "" : title;
  }

  public void add(SRDocumentContent content) {
    if (content != null) {
      contents.add(content);
    }
  }

  public List<SRDocumentContent> getContents() {
    return Collections.unmodifiableList(contents);
  }

  public String asText() {
    StringBuilder builder = new StringBuilder();
    if (!title.isBlank()) {
      builder.append(title).append('\n');
    }
    for (SRDocumentContent content : contents) {
      builder.append(content.asText());
    }
    return builder.toString();
  }
}
