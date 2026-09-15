/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

import org.weasis.core.api.gui.layout.ConstraintSpec;

public class LayoutConstraints extends ConstraintSpec {
  public static final String START = "start";
  public static final String END = "end";

  public LayoutConstraints(int x, int y, int w, int h) {
    super(x, y, w, h);
  }
}
