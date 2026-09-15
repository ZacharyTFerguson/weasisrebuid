/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.pref;

public class Preference {
  private final String code;
  private String value;
  private final String type;
  private final String javaType;
  private final String description;

  public Preference(String code, String value, String type, String javaType, String description) {
    this.code = code;
    this.value = value;
    this.type = type;
    this.javaType = javaType;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getType() {
    return type;
  }

  public boolean isFirstLaunchOnly() {
    return "F".equals(type);
  }

  public boolean isAlwaysFromJson() {
    return "AP".equals(type);
  }

  public String getJavaType() {
    return javaType;
  }

  public String getDescription() {
    return description;
  }
}
