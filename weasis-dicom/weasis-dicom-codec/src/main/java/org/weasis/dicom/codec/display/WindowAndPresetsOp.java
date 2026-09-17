/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.display;

import org.weasis.core.api.image.AbstractOp;

/** DICOM VOI LUT / preset node. Inverse level follows {@code weasis.level.inverse}. */
public class WindowAndPresetsOp extends AbstractOp {
  public static final String P_IMAGE_ELEMENT = "dicom.image";
  public static final String P_WINDOW = "window";
  public static final String P_LEVEL = "level";
  public static final String P_PRESET = "preset";
  public static final String P_INVERT = "invert";

  public WindowAndPresetsOp() {
    super("op.window.presets.dicom");
    setParam(P_INVERT, Boolean.parseBoolean(System.getProperty("weasis.level.inverse", "true")));
  }

  public static boolean levelInverse() {
    return Boolean.parseBoolean(System.getProperty("weasis.level.inverse", "true"));
  }
}
