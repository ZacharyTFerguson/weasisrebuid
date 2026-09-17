/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.dialog;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireManager;
import org.weasis.acquire.explorer.ImportGrouping;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel;

/**
 * Import grouping chrome: current series, or new series via {@link ImportGrouping} (none / date /
 * name).
 */
public class AcquireImportDialog {

  public enum Placement {
    CURRENT_SERIES,
    NEW_SERIES
  }

  private static final DateTimeFormatter DATE_NAME =
      DateTimeFormatter.ofPattern("yyyyMMdd-HHmm").withZone(ZoneOffset.UTC);

  private ImportGrouping grouping = ImportGrouping.NONE;
  private Duration maxGap = Duration.ofHours(1);
  private Placement placement = Placement.NEW_SERIES;
  private String currentSeries = "Series";

  public ImportGrouping grouping() {
    return grouping;
  }

  public void setGrouping(ImportGrouping grouping) {
    this.grouping = grouping == null ? ImportGrouping.NONE : grouping;
  }

  public Duration maxGap() {
    return maxGap;
  }

  public void setMaxGap(Duration maxGap) {
    this.maxGap = maxGap == null ? Duration.ofHours(1) : maxGap;
  }

  public Placement placement() {
    return placement;
  }

  public void setPlacement(Placement placement) {
    this.placement = placement == null ? Placement.NEW_SERIES : placement;
  }

  public String currentSeries() {
    return currentSeries;
  }

  public void setCurrentSeries(String currentSeries) {
    this.currentSeries =
        currentSeries == null || currentSeries.isBlank() ? "Series" : currentSeries;
  }

  public List<List<Path>> groups(List<Path> files) {
    if (placement == Placement.CURRENT_SERIES) {
      if (files == null || files.isEmpty()) {
        return List.of();
      }
      return List.of(List.copyOf(files));
    }
    return ImportGrouping.group(files, grouping, maxGap);
  }

  public String seriesName(List<Path> group, int index) {
    if (placement == Placement.CURRENT_SERIES) {
      return currentSeries;
    }
    if (group == null || group.isEmpty()) {
      return "Series " + (index + 1);
    }
    Path first = group.get(0);
    if (grouping == ImportGrouping.NAME) {
      String key = nameKey(first);
      return key.isBlank() ? "Series " + (index + 1) : key;
    }
    if (grouping == ImportGrouping.DATE) {
      return "Series " + DATE_NAME.format(Instant.ofEpochMilli(mtime(first)));
    }
    return stem(first);
  }

  public void importInto(AcquireCentralThumbnailModel central, List<Path> files) {
    if (central == null) {
      return;
    }
    List<List<Path>> buckets = groups(files);
    for (int i = 0; i < buckets.size(); i++) {
      List<Path> group = buckets.get(i);
      String series = seriesName(group, i);
      for (Path path : group) {
        central.add(series, path);
      }
    }
  }

  public void importInto(
      AcquireManager manager, AcquireCentralThumbnailModel central, List<Path> files) {
    importInto(central, files);
    if (manager == null || files == null) {
      return;
    }
    for (Path path : files) {
      AcquireImageInfo info = new AcquireImageInfo();
      info.setFile(path);
      manager.addImage(info);
    }
  }

  static String stem(Path path) {
    String name = path.getFileName().toString();
    int dot = name.lastIndexOf('.');
    return dot < 0 ? name : name.substring(0, dot);
  }

  static String nameKey(Path path) {
    return stem(path).replaceAll("\\d+$", "").toLowerCase(Locale.ROOT);
  }

  static long mtime(Path path) {
    try {
      return java.nio.file.Files.getLastModifiedTime(path).toMillis();
    } catch (Exception e) {
      return 0L;
    }
  }
}
