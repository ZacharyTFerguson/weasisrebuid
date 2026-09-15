/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.internal.mime;

import java.util.Arrays;

public class MagicMimeEntry {
  private final String mimeType;
  private final byte[] magic;
  private final int offset;

  public MagicMimeEntry(String mimeType, int offset, byte[] magic) throws InvalidMagicMimeEntryException {
    if (mimeType == null || mimeType.isBlank() || magic == null || magic.length == 0) {
      throw new InvalidMagicMimeEntryException("invalid magic mime entry");
    }
    this.mimeType = mimeType;
    this.offset = Math.max(0, offset);
    this.magic = magic.clone();
  }

  public String getMimeType() {
    return mimeType;
  }

  public boolean matches(byte[] header) {
    if (header == null || header.length < offset + magic.length) {
      return false;
    }
    return Arrays.equals(magic, 0, magic.length, header, offset, offset + magic.length);
  }
}
