/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media;

import java.io.File;
import java.util.Locale;

/** File → MIME. DICOM magic is WP-2. */
public final class MimeInspector {

  public static final String DICOM_MIME = "application/dicom";
  public static final String DUMMY_MIME = "image/dummy";

  private MimeInspector() {}

  public static String getMimeType(File file) {
    if (file == null) {
      return null;
    }
    String name = file.getName().toLowerCase(Locale.ROOT);
    if (name.endsWith(".dcm") || name.endsWith(".dicom")) {
      return DICOM_MIME;
    }
    if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
      return "image/jpeg";
    }
    if (name.endsWith(".png")) {
      return "image/png";
    }
    if (name.endsWith(".zip")) {
      return "application/zip";
    }
    return "application/octet-stream";
  }
}
