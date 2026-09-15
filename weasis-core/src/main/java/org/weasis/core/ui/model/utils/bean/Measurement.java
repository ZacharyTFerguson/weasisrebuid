/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.bean;

/** Named quantity a graphic can report (length, area, angle, …). */
public class Measurement {

  private final String name;
  private final Integer id;
  private boolean computed;

  public Measurement(String name, Integer id, boolean computed) {
    this.name = name == null ? "" : name;
    this.id = id;
    this.computed = computed;
  }

  public String getName() {
    return name;
  }

  public Integer getId() {
    return id;
  }

  public boolean isComputed() {
    return computed;
  }

  public void setComputed(boolean computed) {
    this.computed = computed;
  }
}
