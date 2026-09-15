/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.weasis.core.api.net.auth.AuthMethod;
import org.weasis.core.api.net.auth.DefaultAuthMethod;
import org.weasis.core.api.service.WProperties;

/**
 * Stores DICOMweb {@link AuthMethod} rows in {@link WProperties} as {@code weasis.auth.ids} plus
 * per-id header/token keys. Blank or illegal ids are skipped; duplicate ids keep the last row.
 */
public final class AuthenticationPersistence {

  public static final String IDS_KEY = "weasis.auth.ids";
  public static final String PREFIX = "weasis.auth.";
  public static final String HEADER_SUFFIX = ".header";
  public static final String TOKEN_SUFFIX = ".token";

  private AuthenticationPersistence() {}

  public static List<AuthMethod> load(WProperties prefs) {
    List<AuthMethod> out = new ArrayList<>();
    if (prefs == null) {
      return List.of();
    }
    String raw = prefs.getProperty(IDS_KEY, "");
    if (raw == null || raw.isBlank()) {
      return List.of();
    }
    Map<String, AuthMethod> unique = new LinkedHashMap<>();
    for (String token : raw.split(",")) {
      String id = sanitizeId(token);
      if (id.isEmpty()) {
        continue;
      }
      String header = prefs.getProperty(PREFIX + id + HEADER_SUFFIX, "Authorization");
      String value = prefs.getProperty(PREFIX + id + TOKEN_SUFFIX, "");
      unique.put(id, new DefaultAuthMethod(id, header, value));
    }
    out.addAll(unique.values());
    return List.copyOf(out);
  }

  public static void save(WProperties prefs, Collection<? extends AuthMethod> methods) {
    if (prefs == null) {
      return;
    }
    Map<String, AuthMethod> unique = new LinkedHashMap<>();
    if (methods != null) {
      for (AuthMethod method : methods) {
        if (method == null) {
          continue;
        }
        String id = sanitizeId(method.getId());
        if (!id.isEmpty()) {
          unique.put(id, method);
        }
      }
    }
    prefs.setProperty(IDS_KEY, String.join(",", unique.keySet()));
    for (AuthMethod method : unique.values()) {
      write(prefs, method);
    }
  }

  public static void upsert(WProperties prefs, AuthMethod method) {
    if (prefs == null || method == null) {
      return;
    }
    String id = sanitizeId(method.getId());
    if (id.isEmpty()) {
      return;
    }
    List<AuthMethod> current = new ArrayList<>(load(prefs));
    boolean replaced = false;
    for (int i = 0; i < current.size(); i++) {
      if (id.equals(sanitizeId(current.get(i).getId()))) {
        current.set(i, method);
        replaced = true;
        break;
      }
    }
    if (!replaced) {
      current.add(method);
    }
    save(prefs, current);
  }

  public static boolean remove(WProperties prefs, String id) {
    if (prefs == null) {
      return false;
    }
    String key = sanitizeId(id);
    if (key.isEmpty()) {
      return false;
    }
    List<AuthMethod> current = new ArrayList<>(load(prefs));
    boolean removed = current.removeIf(existing -> key.equals(sanitizeId(existing.getId())));
    if (removed) {
      prefs.remove(PREFIX + key + HEADER_SUFFIX);
      prefs.remove(PREFIX + key + TOKEN_SUFFIX);
      save(prefs, current);
    }
    return removed;
  }

  static void write(WProperties prefs, AuthMethod method) {
    String id = sanitizeId(method.getId());
    String header = "Authorization";
    String token = "";
    if (method instanceof DefaultAuthMethod def) {
      if (def.header() != null && !def.header().isBlank()) {
        header = def.header();
      }
      token = def.token() == null ? "" : def.token();
    } else {
      Map<String, String> headers = method.authorizationHeaders();
      if (headers != null && !headers.isEmpty()) {
        Map.Entry<String, String> first = headers.entrySet().iterator().next();
        header = first.getKey();
        token = first.getValue() == null ? "" : first.getValue();
      }
    }
    prefs.setProperty(PREFIX + id + HEADER_SUFFIX, header);
    prefs.setProperty(PREFIX + id + TOKEN_SUFFIX, token);
  }

  public static String sanitizeId(String id) {
    if (id == null || id.isBlank()) {
      return "";
    }
    return id.trim().replaceAll("[^A-Za-z0-9._-]", "_");
  }
}
