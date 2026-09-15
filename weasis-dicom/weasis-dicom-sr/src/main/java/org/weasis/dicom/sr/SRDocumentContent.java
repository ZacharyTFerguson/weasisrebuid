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

/** One SR content item (TEXT, NUM, CODE, CONTAINER, IMAGE, …). */
public class SRDocumentContent {

  private String valueType = "";
  private String relationshipType = "";
  private String conceptName = "";
  private String value = "";
  private final List<SRDocumentContent> children = new ArrayList<>();

  public String getValueType() {
    return valueType;
  }

  public void setValueType(String valueType) {
    this.valueType = valueType == null ? "" : valueType;
  }

  public String getRelationshipType() {
    return relationshipType;
  }

  public void setRelationshipType(String relationshipType) {
    this.relationshipType = relationshipType == null ? "" : relationshipType;
  }

  public String getConceptName() {
    return conceptName;
  }

  public void setConceptName(String conceptName) {
    this.conceptName = conceptName == null ? "" : conceptName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value == null ? "" : value;
  }

  public void add(SRDocumentContent child) {
    if (child != null) {
      children.add(child);
    }
  }

  public List<SRDocumentContent> getChildren() {
    return Collections.unmodifiableList(children);
  }

  public String asText() {
    return asText(0);
  }

  String asText(int indent) {
    String pad = " ".repeat(Math.max(0, indent));
    StringBuilder builder = new StringBuilder();
    builder.append(pad);
    if (!conceptName.isBlank()) {
      builder.append(conceptName);
    } else if (!valueType.isBlank()) {
      builder.append(valueType);
    }
    if (!value.isBlank()) {
      if (builder.length() > pad.length()) {
        builder.append(": ");
      }
      builder.append(value);
    }
    builder.append('\n');
    for (SRDocumentContent child : children) {
      builder.append(child.asText(indent + 2));
    }
    return builder.toString();
  }
}
