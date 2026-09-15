/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.mf;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;
import org.weasis.dicom.explorer.mf.ManifestParser.ManifestSeries;

/**
 * WADO/MF query result: HTTP status plus a {@link DicomModel} filled from a launch manifest so the
 * explorer can list series before pixel data arrives.
 */
public class DicomModelQueryResult {

  private final DicomModel model;
  private final int httpStatus;

  public DicomModelQueryResult(DicomModel model) {
    this(model, 200);
  }

  public DicomModelQueryResult(DicomModel model, int httpStatus) {
    this.model = Objects.requireNonNull(model, "model");
    this.httpStatus = httpStatus;
  }

  public DicomModel model() {
    return model;
  }

  public int httpStatus() {
    return httpStatus;
  }

  public boolean success() {
    return httpStatus >= 200 && httpStatus < 300;
  }

  public boolean add(ImportedInstance instance) {
    return model.addInstance(instance);
  }

  public List<String> seriesUids() {
    LinkedHashSet<String> uids = new LinkedHashSet<>();
    for (ImportedInstance inst : model.getInstances()) {
      uids.add(inst.seriesUid());
    }
    return List.copyOf(uids);
  }

  public int apply(List<ManifestSeries> series) {
    if (series == null) {
      return 0;
    }
    int added = 0;
    for (ManifestSeries row : series) {
      added += addRow(row);
    }
    return added;
  }

  int addRow(ManifestSeries row) {
    if (row == null || !add(placeholder(row))) {
      return 0;
    }
    return 1;
  }

  static ImportedInstance placeholder(ManifestSeries row) {
    String seriesUid = row.seriesUid();
    return new ImportedInstance(
        row.patientId(),
        row.patientId(),
        row.studyUid(),
        seriesUid,
        seriesUid + ".1",
        "1.2.840.10008.10.0.2.2.1.1",
        "",
        "",
        "",
        1,
        1,
        new File(seriesUid + ".dcm"),
        "application/dicom");
  }
}
