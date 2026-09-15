/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec;

/** Grayscale / color GSPS. Applied only when {@code weasis.apply.latest.pr} is true. */
public class PRSpecialElement extends DicomSpecialElement {
  public PRSpecialElement(DcmMediaReader mediaIO) {
    super(mediaIO);
    setMimeType(DicomMime.PR_DICOM);
  }

  public static boolean applyLatestPr() {
    return Boolean.parseBoolean(System.getProperty("weasis.apply.latest.pr", "false"));
  }
}
