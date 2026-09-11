/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * MX-01: a checked study retrieves STUDY-level (all series). Expand + check some series →
 * SERIES-level. A checked study wins over its series regardless of click order.
 */
public final class RetrieveSelection {

  public enum Level {
    STUDY,
    SERIES
  }

  private final Set<String> checkedStudies = new LinkedHashSet<>();
  private final Set<String> checkedSeries = new LinkedHashSet<>();

  public void checkStudy(String studyUid) {
    checkedStudies.add(studyUid);
  }

  public void checkSeries(String studyUid, String seriesUid) {
    checkedSeries.add(studyUid + "/" + seriesUid);
  }

  public Level levelFor(String studyUid) {
    if (checkedStudies.contains(studyUid)) {
      return Level.STUDY;
    }
    return Level.SERIES;
  }

  public boolean retrievesAllSeries(String studyUid) {
    return levelFor(studyUid) == Level.STUDY;
  }
}
