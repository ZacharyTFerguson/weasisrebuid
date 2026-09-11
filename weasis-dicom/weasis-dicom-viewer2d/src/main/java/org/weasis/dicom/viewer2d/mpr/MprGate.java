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

/** Basic 3D (MPR/MIP) and 3D Viewer disabled when the series has fewer than 5 images. */
public final class MprGate {

  public static final int MIN_IMAGES = 5;

  private MprGate() {}

  public static boolean enabled(int imageCount) {
    return imageCount >= MIN_IMAGES;
  }
}
