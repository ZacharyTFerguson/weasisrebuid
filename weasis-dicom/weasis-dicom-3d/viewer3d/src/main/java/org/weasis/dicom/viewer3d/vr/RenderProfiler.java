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

public class RenderProfiler {

  private long lastNanos;
  private int frames;

  public void frame(long nanos) {
    lastNanos = nanos;
    frames++;
  }

  public long getLastNanos() {
    return lastNanos;
  }

  public int getFrames() {
    return frames;
  }
}
