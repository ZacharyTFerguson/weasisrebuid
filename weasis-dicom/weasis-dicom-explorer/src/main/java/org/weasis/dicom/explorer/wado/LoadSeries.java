/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.eclipse.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import java.util.ArrayList;
import java.util.List;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.ArcQuery;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Instance;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.QueryMode;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Series;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Study;

/** One series retrieve job from a parsed manifest. */
public class LoadSeries {

  private final ArcQuery arc;
  private final Study study;
  private final Series series;
  private final boolean bulk;
  private final String bulkUrl;
  private final List<String> instanceUrls;
  private final String completionUrl;
  private final DownloadPriority priority;

  public LoadSeries(
      ArcQuery arc,
      Study study,
      Series series,
      boolean bulk,
      String bulkUrl,
      List<String> instanceUrls,
      String completionUrl) {
    this.arc = arc;
    this.study = study;
    this.series = series;
    this.bulk = bulk;
    this.bulkUrl = bulkUrl == null ? "" : bulkUrl;
    this.instanceUrls = List.copyOf(instanceUrls == null ? List.of() : instanceUrls);
    this.completionUrl = completionUrl == null ? "" : completionUrl;
    this.priority = DownloadPriority.of(series);
  }

  public static LoadSeries from(ArcQuery arc, Study study, Series series, LoadRemoteDicomURL urls) {
    LoadRemoteDicomURL builder = urls == null ? new LoadRemoteDicomURL() : urls;
    boolean dicomWeb = arc != null && arc.queryMode() == QueryMode.DICOM_WEB;
    boolean listed = series != null && series.instancesListed();
    boolean bulk = dicomWeb && !listed && DicomManager.seriesBulk(arc);
    if (bulk) {
      return new LoadSeries(
          arc, study, series, true, builder.wadoRsSeries(arc, study, series), List.of(), "");
    }
    if (dicomWeb && !listed) {
      return new LoadSeries(
          arc,
          study,
          series,
          false,
          "",
          List.of(),
          ManifestCompletion.qidoInstancesUrl(
              arc.baseUrl(),
              study == null ? "" : study.studyInstanceUid(),
              series == null ? "" : series.seriesInstanceUid()));
    }
    List<String> instanceUrls = new ArrayList<>();
    if (series != null) {
      for (Instance instance : series.instances()) {
        instanceUrls.add(builder.retrieveUrl(arc, study, series, instance));
      }
    }
    return new LoadSeries(arc, study, series, false, "", instanceUrls, "");
  }

  public ArcQuery arc() {
    return arc;
  }

  public Study study() {
    return study;
  }

  public Series series() {
    return series;
  }

  public boolean bulk() {
    return bulk;
  }

  public String bulkUrl() {
    return bulkUrl;
  }

  public List<String> instanceUrls() {
    return instanceUrls;
  }

  public String completionUrl() {
    return completionUrl;
  }

  public DownloadPriority priority() {
    return priority;
  }

  public int plannedRequests() {
    if (bulk) {
      return 1;
    }
    if (!completionUrl.isEmpty() && instanceUrls.isEmpty()) {
      return 0;
    }
    return instanceUrls.size();
  }
}
