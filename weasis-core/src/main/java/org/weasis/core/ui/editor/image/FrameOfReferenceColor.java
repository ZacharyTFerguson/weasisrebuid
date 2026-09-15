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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/** Stable border color for a Frame of Reference UID (MX-14 synch group). */
public class FrameOfReferenceColor {

  private static final Color[] PALETTE = {
    Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.GREEN, Color.PINK
  };

  private final Map<String, Color> colors = new HashMap<>();

  public Color colorFor(String uid) {
    if (blank(uid)) {
      return Color.DARK_GRAY;
    }
    Color existing = colors.get(uid);
    if (existing != null) {
      return existing;
    }
    return assign(uid);
  }

  Color assign(String uid) {
    Color next = PALETTE[colors.size() % PALETTE.length];
    colors.put(uid, next);
    return next;
  }

  static boolean blank(String uid) {
    return uid == null || uid.isBlank();
  }
}
