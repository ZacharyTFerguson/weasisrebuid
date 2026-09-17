/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import java.util.Locale;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;

/** SR/DOC last, otherwise series number then description — same idea as explorer series sort. */
public class DownloadPriority implements Comparable<DownloadPriority> {

  private final int rank;
  private final int seriesNumber;
  private final String description;

  public DownloadPriority(int rank, int seriesNumber, String description) {
    this.rank = rank;
    this.seriesNumber = seriesNumber;
    this.description = description == null ? "" : description;
  }

  public static DownloadPriority of(Series series) {
    String modality = series == null ? "" : String.valueOf(series.modality());
    String upper = modality.toUpperCase(Locale.ROOT);
    int rank = 0;
    if ("SR".equals(upper) || "DOC".equals(upper) || "KO".equals(upper) || "PR".equals(upper)) {
      rank = 100;
    }
    int number = 0;
    if (series != null && series.seriesNumber() != null && !series.seriesNumber().isBlank()) {
      try {
        number = Integer.parseInt(series.seriesNumber().trim());
      } catch (NumberFormatException ignored) {
        number = 0;
      }
    }
    String desc = series == null ? "" : series.seriesDescription();
    return new DownloadPriority(rank, number, desc);
  }

  public int rank() {
    return rank;
  }

  @Override
  public int compareTo(DownloadPriority other) {
    if (other == null) {
      return -1;
    }
    int byRank = Integer.compare(rank, other.rank);
    if (byRank != 0) {
      return byRank;
    }
    int byNumber = Integer.compare(seriesNumber, other.seriesNumber);
    if (byNumber != 0) {
      return byNumber;
    }
    return description.compareToIgnoreCase(other.description);
  }
}
