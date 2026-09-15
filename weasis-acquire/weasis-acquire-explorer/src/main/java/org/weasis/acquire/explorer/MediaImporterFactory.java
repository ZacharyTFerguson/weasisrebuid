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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.weasis.acquire.explorer.core.bean.SeriesGroup;
import org.weasis.core.api.media.data.SeriesImporter;

/** Imports still files into {@link AcquireManager}, optionally into a {@link SeriesGroup}. */
public class MediaImporterFactory implements SeriesImporter {

  private volatile boolean stopped;

  public List<AcquireImageInfo> importStills(AcquireManager manager, List<Path> files) {
    return importStills(manager, null, files);
  }

  public List<AcquireImageInfo> importStills(
      AcquireManager manager, SeriesGroup series, List<Path> files) {
    List<AcquireImageInfo> imported = new ArrayList<>();
    if (manager == null || files == null) {
      return imported;
    }
    for (Path path : files) {
      if (stopped) {
        break;
      }
      if (!StillFormats.isStill(path)) {
        continue;
      }
      AcquireImageInfo info = new AcquireImageInfo();
      info.setFile(path);
      info.setStatus(AcquireImageStatus.TO_PUBLISH);
      manager.addImage(info);
      if (series != null) {
        series.add(info);
      }
      imported.add(info);
    }
    if (series != null) {
      manager.addSeries(series);
    }
    return imported;
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public void stop() {
    stopped = true;
  }
}
