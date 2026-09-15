/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

/** DICOMweb node (QIDO/WADO/STOW base URL). */
public final class DicomWebNode {

  private final String description;
  private final String baseUrl;

  public DicomWebNode(String description, String baseUrl) {
    this.description = description;
    this.baseUrl = baseUrl;
  }

  public String description() {
    return description;
  }

  public String baseUrl() {
    return baseUrl;
  }

  public boolean dicomWeb() {
    return true;
  }
}
