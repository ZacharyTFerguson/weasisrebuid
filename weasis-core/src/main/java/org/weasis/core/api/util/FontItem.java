/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.awt.Font;

public enum FontItem {
  DEFAULT(Font.DIALOG, 12),
  LARGE(Font.DIALOG, 16),
  SMALL(Font.DIALOG, 10),
  MONO(Font.MONOSPACED, 12);

  private final String family;
  private final int size;

  FontItem(String family, int size) {
    this.family = family;
    this.size = size;
  }

  public Font getFont() {
    return new Font(family, Font.PLAIN, size);
  }

  public int getFontSize() {
    return size;
  }
}
