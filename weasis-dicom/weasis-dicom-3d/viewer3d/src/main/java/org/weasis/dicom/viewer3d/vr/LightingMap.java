/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d.vr;

public class LightingMap {

  private final float[] ambient = {0.2f, 0.2f, 0.2f};
  private final float[] diffuse = {0.8f, 0.8f, 0.8f};

  public float[] getAmbient() {
    return ambient;
  }

  public float[] getDiffuse() {
    return diffuse;
  }
}
