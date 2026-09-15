/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.layout;

public class MigCell {
  private final ConstraintSpec spec;
  private Object component;

  public MigCell(ConstraintSpec spec) {
    this.spec = spec;
  }

  public ConstraintSpec getSpec() {
    return spec;
  }

  public Object getComponent() {
    return component;
  }

  public void setComponent(Object component) {
    this.component = component;
  }
}
