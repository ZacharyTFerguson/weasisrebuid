/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

/** Tokens for {@code dcmview2d:reset} ({@code -a} or {@code winLevel|zoom|pan|rotation}). */
public enum ResetTools {
  ALL,
  WINLEVEL,
  ZOOM,
  PAN,
  ROTATION;

  public static ResetTools fromCommand(String token) {
    if (token == null || token.isBlank() || "-a".equals(token) || "all".equalsIgnoreCase(token)) {
      return ALL;
    }
    return switch (token) {
      case "winLevel", "window", "level" -> WINLEVEL;
      case "zoom" -> ZOOM;
      case "pan" -> PAN;
      case "rotation" -> ROTATION;
      default -> ALL;
    };
  }
}
