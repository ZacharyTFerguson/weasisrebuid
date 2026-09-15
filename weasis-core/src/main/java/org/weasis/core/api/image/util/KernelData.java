/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image.util;

public class KernelData {
  public static final KernelData NONE = new KernelData("None", 1, new float[] {1f});
  public static final KernelData SHARPEN =
      new KernelData("Sharpen", 3, new float[] {0, -1, 0, -1, 5, -1, 0, -1, 0});

  private final String name;
  private final int size;
  private final float[] data;

  public KernelData(String name, int size, float[] data) {
    this.name = name;
    this.size = size;
    this.data = data == null ? new float[] {1f} : data.clone();
  }

  public String getName() {
    return name;
  }

  public int getKernelSize() {
    return size;
  }

  public float[] getData() {
    return data.clone();
  }
}
