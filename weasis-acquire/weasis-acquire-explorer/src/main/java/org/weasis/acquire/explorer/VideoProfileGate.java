/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.util.Locale;

/**
 * Video SHALL include MPEG-2 ({@code video/mpeg}) as well as MPEG-4 AVC/H.264 High ≤4.2 and HEVC
 * Main/Main10; other MPEG-4 profiles SHALL be rejected.
 */
public final class VideoProfileGate {

  private VideoProfileGate() {}

  public static boolean accept(String mime, String profile, String level) {
    if (mime == null) {
      return false;
    }
    String m = mime.toLowerCase(Locale.ROOT);
    if ("video/mpeg".equals(m) || "video/mp2t".equals(m)) {
      return true;
    }
    String p = profile == null ? "" : profile.trim();
    String lv = level == null ? "" : level.trim();
    if (isAvc(m, p)) {
      return "High".equalsIgnoreCase(p) && levelAtMost(lv, 4, 2);
    }
    if (isHevc(m, p)) {
      return "Main".equalsIgnoreCase(p)
          || "Main10".equalsIgnoreCase(p)
          || "Main 10".equalsIgnoreCase(p);
    }
    return false;
  }

  static boolean isAvc(String mime, String profile) {
    return mime.contains("mp4")
        || mime.contains("avc")
        || "avc".equalsIgnoreCase(profile)
        || mime.equals("video/h264");
  }

  static boolean isHevc(String mime, String profile) {
    return mime.contains("hevc")
        || mime.contains("h265")
        || "Main".equalsIgnoreCase(profile)
        || "Main10".equalsIgnoreCase(profile);
  }

  static boolean levelAtMost(String level, int major, int minor) {
    if (level == null || level.isBlank()) {
      return false;
    }
    String n = level.replace("L", "").replace("l", "").trim();
    String[] parts = n.split("\\.");
    try {
      int maj = Integer.parseInt(parts[0]);
      int min = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
      if (maj < major) {
        return true;
      }
      if (maj > major) {
        return false;
      }
      return min <= minor;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
