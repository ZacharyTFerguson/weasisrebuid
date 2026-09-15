/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import java.io.InputStream;
import java.net.URL;

public final class DicomResource {
  private DicomResource() {}

  public static URL getResource(String name) {
    if (name == null) {
      return null;
    }
    return DicomResource.class.getResource(name.startsWith("/") ? name : "/" + name);
  }

  public static InputStream getResourceAsStream(String name) {
    URL url = getResource(name);
    try {
      return url == null ? null : url.openStream();
    } catch (Exception e) {
      return null;
    }
  }
}
