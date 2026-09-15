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

import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/**
 * Series thumbnail URL: {@code DirectDownloadThumbnail} or WADO-URI {@code contentType=image/jpeg}.
 */
public class ThumbnailManager {

  public String thumbnailUrl(ArcQuery arc, Study study, Series series) {
    if (series != null && series.directDownloadThumbnail() != null && !series.directDownloadThumbnail().isBlank()) {
      return new LoadRemoteDicomURL().join(arc == null ? "" : arc.baseUrl(), series.directDownloadThumbnail());
    }
    if (arc != null && arc.queryMode() == QueryMode.DICOM_WEB) {
      String seriesUrl = new LoadRemoteDicomURL().wadoRsSeries(arc, study, series);
      return seriesUrl + "/thumbnail";
    }
    ReaderParams params = new ReaderParams();
    params.setContentType("image/jpeg");
    return new LoadRemoteDicomURL().wadoUri(arc, study, series, null, params);
  }
}
