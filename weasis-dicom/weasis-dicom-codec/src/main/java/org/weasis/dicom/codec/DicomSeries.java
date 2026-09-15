/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec;

import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.TagW;

public class DicomSeries extends Series<DicomImageElement> {

  public DicomSeries() {
    this(null);
  }

  public DicomSeries(String seriesUid) {
    super(seriesUid == null ? java.util.UUID.randomUUID().toString() : seriesUid);
    setMimeType(DicomMime.SERIES_DICOM);
  }

  public String getSeriesInstanceUID() {
    Object v = getTagValue(TagW.SeriesInstanceUID);
    return v == null ? "" : v.toString();
  }
}
