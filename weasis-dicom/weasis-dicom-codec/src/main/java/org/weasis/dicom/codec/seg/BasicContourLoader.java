/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import java.awt.geom.Area;
import java.util.Collections;
import java.util.List;

public class BasicContourLoader implements LazyContourLoader {
  private final List<List<Area>> frames;

  public BasicContourLoader(List<List<Area>> frames) {
    this.frames = frames == null ? List.of() : List.copyOf(frames);
  }

  @Override
  public List<Area> getContours(int frame) {
    if (frame < 0 || frame >= frames.size()) {
      return List.of();
    }
    List<Area> c = frames.get(frame);
    return c == null ? List.of() : Collections.unmodifiableList(c);
  }

  @Override
  public boolean isLoaded(int frame) {
    return frame >= 0 && frame < frames.size();
  }
}
