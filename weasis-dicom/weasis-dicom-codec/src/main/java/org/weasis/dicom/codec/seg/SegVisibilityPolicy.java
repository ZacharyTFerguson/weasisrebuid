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

import java.util.List;
import java.util.Locale;

/**
 * MX-02: hide-all greys keyword and hide-from rules. Keyword match is Series Description / Content
 * Description / Content Label or every segment (label / description / algorithm name). Ignore case,
 * spaces, underscores, hyphens.
 */
public final class SegVisibilityPolicy {

  public static final String HIDE_ALL = "weasis.dicom.seg.hide.all";
  public static final String HIDE_KEYWORDS = "weasis.dicom.seg.hide.keywords";
  public static final String HIDE_COUNT = "weasis.dicom.seg.hide.count";
  public static final String DEFAULT_KEYWORDS =
      "table removal, table segmentation, tabletop, couch, patient support, bed removal";
  public static final int DEFAULT_HIDE_COUNT = 3;

  private boolean hideAll;
  private int hideCount = DEFAULT_HIDE_COUNT;
  private String keywords = DEFAULT_KEYWORDS;

  public boolean hideAll() {
    return hideAll;
  }

  public void setHideAll(boolean hideAll) {
    this.hideAll = hideAll;
  }

  public boolean keywordRulesEnabled() {
    return !hideAll;
  }

  public boolean hideFromCountEnabled() {
    return !hideAll && hideCount > 0;
  }

  public int hideCount() {
    return hideCount;
  }

  public void setHideCount(int hideCount) {
    this.hideCount = Math.max(0, hideCount);
  }

  public void restoreDefaults() {
    hideAll = false;
    hideCount = DEFAULT_HIDE_COUNT;
    keywords = DEFAULT_KEYWORDS;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords == null ? DEFAULT_KEYWORDS : keywords;
  }

  public String keywords() {
    return keywords;
  }

  public boolean hiddenByKeyword(
      String seriesDescription,
      String contentDescription,
      String contentLabel,
      List<String> everySegmentField) {
    if (hideAll || keywords.isBlank()) {
      return false;
    }
    for (String raw : keywords.split(",")) {
      String needle = normalize(raw);
      if (needle.isEmpty()) {
        continue;
      }
      if (contains(seriesDescription, needle)
          || contains(contentDescription, needle)
          || contains(contentLabel, needle)) {
        return true;
      }
      if (everySegmentField != null && !everySegmentField.isEmpty()) {
        boolean all = true;
        for (String field : everySegmentField) {
          if (!contains(field, needle)) {
            all = false;
            break;
          }
        }
        if (all) {
          return true;
        }
      }
    }
    return false;
  }

  static String normalize(String s) {
    if (s == null) {
      return "";
    }
    return s.toLowerCase(Locale.ROOT).replace(" ", "").replace("_", "").replace("-", "").trim();
  }

  static boolean contains(String haystack, String needle) {
    return normalize(haystack).contains(needle);
  }
}
