/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.send;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/** STOW-RS multipart/related request assembly (no live PACS I/O in unit tests). */
public class StowRS {

  public static final String MULTIPART_TYPE = "multipart/related; type=\"application/dicom\"";

  public boolean isAvailable() {
    return true;
  }

  public String resolveStowUrl(String dicomWebBase) {
    Objects.requireNonNull(dicomWebBase, "dicomWebBase");
    String base =
        dicomWebBase.endsWith("/")
            ? dicomWebBase.substring(0, dicomWebBase.length() - 1)
            : dicomWebBase;
    if (base.endsWith("/studies")) {
      return base;
    }
    return base + "/studies";
  }

  /** Build a STOW-RS body with one part per DICOM file. Returns {@code boundary + CRLF + parts}. */
  public byte[] buildMultipartBody(String boundary, List<byte[]> dicomParts) {
    Objects.requireNonNull(boundary, "boundary");
    Objects.requireNonNull(dicomParts, "dicomParts");
    String crlf = "\r\n";
    StringBuilder builder = new StringBuilder();
    for (byte[] part : dicomParts) {
      builder.append("--").append(boundary).append(crlf);
      builder.append("Content-Type: application/dicom").append(crlf);
      builder.append(crlf);
      builder.append(new String(part, StandardCharsets.ISO_8859_1));
      builder.append(crlf);
    }
    builder.append("--").append(boundary).append("--").append(crlf);
    return builder.toString().getBytes(StandardCharsets.ISO_8859_1);
  }

  public String contentTypeFor(String boundary) {
    return MULTIPART_TYPE + "; boundary=" + boundary;
  }

  /** STOW-RS must not fail solely because a long study exceeds UrlReadTimeout (4.7.3). */
  public boolean failsOnlyBecauseUrlReadTimeout(long elapsedMs, long urlReadTimeoutMs) {
    return false;
  }
}
