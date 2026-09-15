/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;
import org.weasis.dicom.explorer.wado.ManifestModelBuilder.Manifest;

/**
 * {@code dicom:get -w} loader. Gzip or plain; JSON vs XML is sniffed from the document, not the
 * file extension (weasis.org integration).
 */
public class LoadRemoteDicomManifest {

  public Manifest parseText(String text) throws DownloadException {
    return ManifestModelBuilder.parseDocument(text);
  }

  public Manifest parseBytes(byte[] bytes) throws DownloadException {
    if (bytes == null || bytes.length == 0) {
      throw new DownloadException("empty manifest");
    }
    byte[] payload = isGzip(bytes) ? gunzip(bytes) : bytes;
    return parseText(new String(payload, StandardCharsets.UTF_8));
  }

  public Manifest parseFile(Path path) throws DownloadException {
    try {
      return parseBytes(Files.readAllBytes(path));
    } catch (IOException e) {
      throw new DownloadException("manifest file " + path, e);
    }
  }

  public static boolean isGzip(byte[] bytes) {
    return bytes != null
        && bytes.length >= 2
        && (bytes[0] & 0xff) == 0x1f
        && (bytes[1] & 0xff) == 0x8b;
  }

  static byte[] gunzip(byte[] bytes) throws DownloadException {
    try (GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
      return in.readAllBytes();
    } catch (IOException e) {
      throw new DownloadException("gzip manifest", e);
    }
  }
}
