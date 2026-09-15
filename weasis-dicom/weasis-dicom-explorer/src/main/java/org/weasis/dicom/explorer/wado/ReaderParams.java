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

/** WADO retrieve options carried with a series job. */
public class ReaderParams {

  private String contentType = "application/dicom";
  private String additionalParameters = "";
  private boolean requireOnlySopInstanceUid;

  public String contentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType == null || contentType.isBlank() ? "application/dicom" : contentType;
  }

  public String additionalParameters() {
    return additionalParameters;
  }

  public void setAdditionalParameters(String additionalParameters) {
    this.additionalParameters = additionalParameters == null ? "" : additionalParameters;
  }

  public boolean requireOnlySopInstanceUid() {
    return requireOnlySopInstanceUid;
  }

  public void setRequireOnlySopInstanceUid(boolean requireOnlySopInstanceUid) {
    this.requireOnlySopInstanceUid = requireOnlySopInstanceUid;
  }
}
