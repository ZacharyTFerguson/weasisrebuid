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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.weasis.acquire.explorer.core.bean.Global;
import org.weasis.acquire.explorer.core.bean.SeriesGroup;

/** Patient context and imported non-DICOM images for Dicomizer. */
public class AcquireManager {

  private final List<AcquireImageInfo> images = new ArrayList<>();
  private final List<SeriesGroup> seriesGroups = new ArrayList<>();
  private final Global global = new Global();
  private String patientXml = "";
  private PatientDemographics demographics = PatientDemographics.empty();

  public void loadPatientContext(String xml) {
    this.patientXml = xml == null ? "" : xml;
    if (patientXml.isBlank()) {
      setDemographics(PatientDemographics.empty());
      return;
    }
    try {
      setDemographics(AcquirePatientCommand.parseXml(patientXml));
    } catch (Exception ignored) {
      setDemographics(PatientDemographics.empty());
    }
  }

  public String getPatientXml() {
    return patientXml;
  }

  public PatientDemographics getDemographics() {
    return demographics;
  }

  public void setDemographics(PatientDemographics demographics) {
    this.demographics = demographics == null ? PatientDemographics.empty() : demographics;
    global.init(this.demographics);
  }

  public Global getGlobal() {
    return global;
  }

  public void addImage(AcquireImageInfo info) {
    if (info != null) {
      images.add(info);
    }
  }

  public List<AcquireImageInfo> getImages() {
    return Collections.unmodifiableList(images);
  }

  public void addSeries(SeriesGroup group) {
    if (group != null && !seriesGroups.contains(group)) {
      seriesGroups.add(group);
    }
  }

  public List<SeriesGroup> getSeriesGroups() {
    return Collections.unmodifiableList(seriesGroups);
  }
}
