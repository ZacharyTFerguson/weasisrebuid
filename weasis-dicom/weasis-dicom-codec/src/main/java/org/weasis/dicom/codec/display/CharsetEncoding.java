/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.display;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/** DICOM Specific Character Set → Java charset. */
public enum CharsetEncoding {
  DEFAULT("", StandardCharsets.UTF_8),
  ISO_IR_6("ISO_IR 6", StandardCharsets.US_ASCII),
  ISO_IR_100("ISO_IR 100", Charset.forName("ISO-8859-1")),
  ISO_IR_101("ISO_IR 101", Charset.forName("ISO-8859-2")),
  ISO_IR_192("ISO_IR 192", StandardCharsets.UTF_8),
  GB18030("GB18030", Charset.forName("GB18030"));

  private final String dicomCode;
  private final Charset charset;

  CharsetEncoding(String dicomCode, Charset charset) {
    this.dicomCode = dicomCode;
    this.charset = charset;
  }

  public String dicomCode() {
    return dicomCode;
  }

  public Charset charset() {
    return charset;
  }

  public static CharsetEncoding parse(String specificCharacterSet) {
    if (specificCharacterSet == null || specificCharacterSet.isBlank()) {
      return DEFAULT;
    }
    String code = specificCharacterSet.trim().toUpperCase(Locale.ROOT);
    for (CharsetEncoding enc : values()) {
      if (enc.dicomCode.equalsIgnoreCase(specificCharacterSet.trim())
          || enc.name().replace('_', ' ').equals(code)) {
        return enc;
      }
    }
    return DEFAULT;
  }
}
