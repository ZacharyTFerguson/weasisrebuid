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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SegVolumeCache {
  private final Map<String, SegmentationVolume> cache = new ConcurrentHashMap<>();

  public SegmentationVolume get(String sopInstanceUid) {
    return sopInstanceUid == null ? null : cache.get(sopInstanceUid);
  }

  public void put(String sopInstanceUid, SegmentationVolume volume) {
    if (sopInstanceUid != null && volume != null) {
      cache.put(sopInstanceUid, volume);
    }
  }

  public void remove(String sopInstanceUid) {
    cache.remove(sopInstanceUid);
  }
}
