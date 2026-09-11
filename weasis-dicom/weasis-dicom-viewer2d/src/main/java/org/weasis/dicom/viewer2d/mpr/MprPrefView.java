/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

/**
 * Prefs &gt; Viewer &gt; MPR. Auto center axes is a three-value combo. Tutorial omits Never — Have
 * follows tag v4.7.3 {@code mpr.crosshair.mode} default 1.
 */
public final class MprPrefView {

  public static final String CROSSHAIR_MODE = "mpr.crosshair.mode";

  public enum AutoCenter {
    NEVER(0),
    WHEN_CENTER_HIDDEN(1),
    ALWAYS(2);

    private final int code;

    AutoCenter(int code) {
      this.code = code;
    }

    public int code() {
      return code;
    }

    public static AutoCenter fromCode(int code) {
      return switch (code) {
        case 0 -> NEVER;
        case 2 -> ALWAYS;
        default -> WHEN_CENTER_HIDDEN;
      };
    }
  }

  private AutoCenter autoCenter = AutoCenter.WHEN_CENTER_HIDDEN;
  private int crosshairGap = 5;

  public AutoCenter autoCenter() {
    return autoCenter;
  }

  public void setAutoCenter(AutoCenter autoCenter) {
    this.autoCenter = autoCenter == null ? AutoCenter.WHEN_CENTER_HIDDEN : autoCenter;
  }

  public void setAutoCenterCode(int code) {
    this.autoCenter = AutoCenter.fromCode(code);
  }

  public int autoCenterCode() {
    return autoCenter.code();
  }

  public int crosshairGap() {
    return crosshairGap;
  }

  public void setCrosshairGap(int crosshairGap) {
    this.crosshairGap = Math.max(0, crosshairGap);
  }
}
