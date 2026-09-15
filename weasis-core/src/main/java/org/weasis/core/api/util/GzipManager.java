/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class GzipManager {
  private GzipManager() {}

  public static byte[] gzipCompressToBytes(byte[] input) {
    if (input == null) {
      return new byte[0];
    }
    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out)) {
      gzip.write(input);
      gzip.finish();
      return out.toByteArray();
    } catch (Exception e) {
      return input;
    }
  }

  public static byte[] gzipUncompressToBytes(byte[] input) {
    if (input == null) {
      return new byte[0];
    }
    try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(input))) {
      return gzip.readAllBytes();
    } catch (Exception e) {
      return input;
    }
  }
}
