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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * QIDO Accept is always application/dicom+json. A configured Accept applies to retrieve, not QIDO.
 */
public final class QidoClient {

  public static final String QIDO_ACCEPT = "application/dicom+json";

  public enum ServiceType {
    ALL_RESTFUL,
    QIDO_RS,
    STOW_RS,
    WADO_URI,
    WADO_RS
  }

  private ServiceType serviceType = ServiceType.ALL_RESTFUL;
  private String retrieveAccept;

  public Map<String, String> qidoHeaders() {
    Map<String, String> h = new LinkedHashMap<>();
    h.put("Accept", QIDO_ACCEPT);
    return h;
  }

  public Map<String, String> retrieveHeaders() {
    Map<String, String> h = new LinkedHashMap<>();
    if (retrieveAccept != null && !retrieveAccept.isBlank()) {
      h.put("Accept", retrieveAccept);
    }
    return h;
  }

  public void setRetrieveAccept(String retrieveAccept) {
    this.retrieveAccept = retrieveAccept;
  }

  public ServiceType serviceType() {
    return serviceType;
  }

  public void setServiceType(ServiceType serviceType) {
    this.serviceType = serviceType == null ? ServiceType.ALL_RESTFUL : serviceType;
  }
}
