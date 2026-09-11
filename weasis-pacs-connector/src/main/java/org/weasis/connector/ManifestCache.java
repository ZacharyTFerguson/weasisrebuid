/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.connector;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Manifest Redis cache TTL ~3 minutes; key is a hash of the search criteria. */
public final class ManifestCache {

  public static final Duration TTL = Duration.ofMinutes(3);

  public record Entry(String body, long storedAtMs) {}

  private final Map<String, Entry> store = new ConcurrentHashMap<>();
  private final long ttlMs;

  public ManifestCache() {
    this(TTL.toMillis());
  }

  public ManifestCache(long ttlMs) {
    this.ttlMs = ttlMs;
  }

  public String key(String criteria) {
    try {
      byte[] digest =
          MessageDigest.getInstance("SHA-256").digest(criteria.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
  }

  public void put(String criteria, String body, long nowMs) {
    store.put(key(criteria), new Entry(body, nowMs));
  }

  public String get(String criteria, long nowMs) {
    Entry entry = store.get(key(criteria));
    if (entry == null) {
      return null;
    }
    if (nowMs - entry.storedAtMs() > ttlMs) {
      store.remove(key(criteria));
      return null;
    }
    return entry.body();
  }
}
