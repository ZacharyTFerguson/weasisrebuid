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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Retrieve DIMSE / DICOMweb context, including C-GET transfer syntax map. */
public final class RetrieveContext {

  public static final String CGET_SOP_PREFIX = "CGET_SOP_UID.";

  public enum RetrieveMethod {
    C_MOVE,
    C_GET,
    WADO_URI,
    WADO_RS
  }

  private RetrieveMethod method = RetrieveMethod.C_MOVE;
  private String callingAet = "WEASIS";
  private String calledAet = "PACS";

  public RetrieveMethod method() {
    return method;
  }

  public void setMethod(RetrieveMethod method) {
    this.method = method == null ? RetrieveMethod.C_MOVE : method;
  }

  public String callingAet() {
    return callingAet;
  }

  public void setCallingAet(String callingAet) {
    if (callingAet != null && !callingAet.isBlank()) {
      this.callingAet = callingAet;
    }
  }

  public String calledAet() {
    return calledAet;
  }

  public void setCalledAet(String calledAet) {
    if (calledAet != null && !calledAet.isBlank()) {
      this.calledAet = calledAet;
    }
  }

  public static Map<String, String> loadCGetTransferSyntaxes() throws IOException {
    Properties properties = new Properties();
    try (InputStream in = RetrieveContext.class.getResourceAsStream("/store-tcs.properties")) {
      if (in == null) {
        throw new IOException("missing store-tcs.properties");
      }
      properties.load(in);
    }
    Map<String, String> map = new LinkedHashMap<>();
    for (String name : properties.stringPropertyNames()) {
      if (name.startsWith(CGET_SOP_PREFIX)) {
        String sop = name.substring(CGET_SOP_PREFIX.length());
        map.put(sop, properties.getProperty(name));
      }
    }
    return map;
  }
}
