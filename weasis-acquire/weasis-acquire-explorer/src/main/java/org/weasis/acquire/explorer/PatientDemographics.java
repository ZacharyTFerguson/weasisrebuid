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

/** Inbound patient values from worklist or {@code $acquire:patient}. Not reformatted. */
public record PatientDemographics(
    String patientName,
    String patientId,
    String birthDate,
    String sex,
    String accessionNumber,
    String operatorsName,
    String studyId,
    String issuerOfAccessionNumber) {

  public PatientDemographics(
      String patientName, String patientId, String birthDate, String sex, String accessionNumber) {
    this(patientName, patientId, birthDate, sex, accessionNumber, "", "", "");
  }

  public static PatientDemographics empty() {
    return new PatientDemographics("", "", "", "", "");
  }

  public PatientDemographics withName(String name) {
    return new PatientDemographics(
        name,
        patientId,
        birthDate,
        sex,
        accessionNumber,
        operatorsName,
        studyId,
        issuerOfAccessionNumber);
  }
}
