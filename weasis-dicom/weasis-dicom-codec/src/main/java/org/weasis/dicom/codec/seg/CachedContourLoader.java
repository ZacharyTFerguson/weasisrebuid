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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedContourLoader implements LazyContourLoader {
  private final LazyContourLoader delegate;
  private final Map<Integer, List<Area>> cache = new ConcurrentHashMap<>();

  public CachedContourLoader(LazyContourLoader delegate) {
    this.delegate = delegate;
  }

  @Override
  public List<Area> getContours(int frame) {
    return cache.computeIfAbsent(frame, delegate::getContours);
  }

  @Override
  public boolean isLoaded(int frame) {
    return cache.containsKey(frame) || (delegate != null && delegate.isLoaded(frame));
  }
}
