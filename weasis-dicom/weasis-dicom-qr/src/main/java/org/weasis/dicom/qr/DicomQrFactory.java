/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

/** DIMSE C-FIND/MOVE/GET and DICOMweb QIDO factory. */
public class DicomQrFactory {

  public enum Verb {
    C_FIND,
    C_MOVE,
    C_GET,
    QIDO_RS,
    WADO_URI,
    WADO_RS
  }

  public boolean supportsCFind() {
    return true;
  }

  public boolean supports(Verb verb) {
    return verb != null;
  }

  public RsQuery newRsQuery() {
    return new RsQuery();
  }

  public SearchParameters newSearchParameters() {
    return new SearchParameters();
  }

  public RetrieveContext newRetrieveContext() {
    return new RetrieveContext();
  }
}
