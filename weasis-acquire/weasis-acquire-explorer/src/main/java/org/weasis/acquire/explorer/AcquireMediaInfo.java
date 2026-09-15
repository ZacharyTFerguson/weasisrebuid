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
import org.weasis.acquire.explorer.core.bean.SeriesGroup;

/** Imported non-DICOM item waiting in a {@link SeriesGroup}. */
public class AcquireMediaInfo {

  private Path file;
  private SeriesGroup seriesGroup;
  private AcquireImageStatus status = AcquireImageStatus.TO_PUBLISH;

  public Path getFile() {
    return file;
  }

  public void setFile(Path file) {
    this.file = file;
  }

  public SeriesGroup getSeriesGroup() {
    return seriesGroup;
  }

  public void setSeriesGroup(SeriesGroup seriesGroup) {
    this.seriesGroup = seriesGroup;
  }

  public AcquireImageStatus getStatus() {
    return status;
  }

  public void setStatus(AcquireImageStatus status) {
    this.status = status == null ? AcquireImageStatus.TO_PUBLISH : status;
  }

  public boolean toPublish() {
    return status == AcquireImageStatus.TO_PUBLISH;
  }

  public void markPublished() {
    this.status = AcquireImageStatus.PUBLISHED;
  }
}
