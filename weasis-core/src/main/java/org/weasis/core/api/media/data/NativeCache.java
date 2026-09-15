/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.media.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class NativeCache<K, V> {
  private final int maxEntries;
  private final LinkedHashMap<K, V> map;

  public NativeCache(int maxEntries) {
    this.maxEntries = Math.max(1, maxEntries);
    this.map =
        new LinkedHashMap<>(16, 0.75f, true) {
          @Override
          protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > NativeCache.this.maxEntries;
          }
        };
  }

  public synchronized V get(K key) {
    return map.get(key);
  }

  public synchronized void put(K key, V value) {
    if (key != null && value != null) {
      map.put(key, value);
    }
  }

  public synchronized void remove(K key) {
    map.remove(key);
  }

  public synchronized int size() {
    return map.size();
  }
}
