/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import java.awt.Color;
import java.util.Properties;

/**
 * Typed {@link Properties} used by Weasis prefs. Missing or unparsable values return the supplied
 * default (never throw).
 */
public class WProperties extends Properties {

  public WProperties() {
    super();
  }

  public WProperties(Properties defaults) {
    super(defaults);
  }

  public boolean getBooleanProperty(String key, boolean defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    if ("true".equalsIgnoreCase(raw) || "yes".equalsIgnoreCase(raw) || "1".equals(raw)) {
      return true;
    }
    if ("false".equalsIgnoreCase(raw) || "no".equalsIgnoreCase(raw) || "0".equals(raw)) {
      return false;
    }
    return defaultValue;
  }

  public void putBooleanProperty(String key, boolean value) {
    setProperty(key, Boolean.toString(value));
  }

  public int getIntProperty(String key, int defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(raw.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void putIntProperty(String key, int value) {
    setProperty(key, Integer.toString(value));
  }

  public long getLongProperty(String key, long defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Long.parseLong(raw.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void putLongProperty(String key, long value) {
    setProperty(key, Long.toString(value));
  }

  public float getFloatProperty(String key, float defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Float.parseFloat(raw.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void putFloatProperty(String key, float value) {
    setProperty(key, Float.toString(value));
  }

  public double getDoubleProperty(String key, double defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Double.parseDouble(raw.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public void putDoubleProperty(String key, double value) {
    setProperty(key, Double.toString(value));
  }

  public Color getColorProperty(String key, Color defaultValue) {
    String raw = getProperty(key);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    Color parsed = parseColor(raw.trim());
    return parsed == null ? defaultValue : parsed;
  }

  public void putColorProperty(String key, Color color) {
    if (color == null) {
      remove(key);
      return;
    }
    setProperty(
        key,
        color.getRed() + "$" + color.getGreen() + "$" + color.getBlue() + "$" + color.getAlpha());
  }

  static Color parseColor(String raw) {
    if (raw.startsWith("#") && (raw.length() == 7 || raw.length() == 9)) {
      try {
        int rgb = (int) Long.parseLong(raw.substring(1), 16);
        if (raw.length() == 7) {
          return new Color(rgb);
        }
        int a = (rgb >> 24) & 0xFF;
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new Color(r, g, b, a);
      } catch (NumberFormatException e) {
        return null;
      }
    }
    String[] parts = raw.split("[$]|[,;]");
    if (parts.length < 3) {
      return null;
    }
    try {
      int r = Integer.parseInt(parts[0].trim());
      int g = Integer.parseInt(parts[1].trim());
      int b = Integer.parseInt(parts[2].trim());
      int a = parts.length > 3 ? Integer.parseInt(parts[3].trim()) : 255;
      return new Color(r, g, b, a);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
