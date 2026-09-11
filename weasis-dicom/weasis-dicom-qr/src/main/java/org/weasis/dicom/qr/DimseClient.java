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

/** DIMSE verbs + WADO-URI. C-FIND charset default ISO_IR 192. */
public final class DimseClient {

  public enum Verb {
    C_ECHO,
    C_FIND,
    C_MOVE,
    C_GET,
    C_STORE
  }

  public static final String FIND_CHARSET = "ISO_IR 192";

  public String charset() {
    return FIND_CHARSET;
  }

  public boolean supports(Verb verb) {
    return verb != null;
  }
}
