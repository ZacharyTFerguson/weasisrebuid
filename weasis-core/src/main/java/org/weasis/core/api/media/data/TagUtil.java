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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class TagUtil {
  private TagUtil() {}

  public static String getTagValue(TagReadable readable, TagW tag, String defaultValue) {
    if (readable == null || tag == null) {
      return defaultValue;
    }
    Object v = readable.getTagValue(tag);
    return v == null ? defaultValue : v.toString();
  }

  public static String formatDicomDate(String yyyymmdd) {
    if (yyyymmdd == null || yyyymmdd.length() < 8) {
      return Objects.toString(yyyymmdd, "");
    }
    try {
      return LocalDate.parse(yyyymmdd.substring(0, 8), DateTimeFormatter.BASIC_ISO_DATE)
          .format(DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (Exception e) {
      return yyyymmdd;
    }
  }
}
