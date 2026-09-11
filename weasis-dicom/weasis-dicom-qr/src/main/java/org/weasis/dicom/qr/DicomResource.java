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

/** C-GET SOP Class transfer syntaxes from shipping {@code store-tcs.properties}. */
public final class DicomResource {

  public static final String CGET_SOP_UID = "CGET_SOP_UID";

  private DicomResource() {}

  public static Map<String, String> loadStoreTcs() throws IOException {
    Properties p = new Properties();
    try (InputStream in = DicomResource.class.getResourceAsStream("/store-tcs.properties")) {
      if (in == null) {
        throw new IOException("missing store-tcs.properties");
      }
      p.load(in);
    }
    Map<String, String> map = new LinkedHashMap<>();
    for (String name : p.stringPropertyNames()) {
      map.put(name, p.getProperty(name));
    }
    return map;
  }
}
