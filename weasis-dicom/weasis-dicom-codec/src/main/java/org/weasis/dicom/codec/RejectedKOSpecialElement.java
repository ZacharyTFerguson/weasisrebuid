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

/** Rejection note KO (hidden). */
public class RejectedKOSpecialElement extends AbstractKOSpecialElement {
  public RejectedKOSpecialElement(DcmMediaReader mediaIO) {
    super(mediaIO);
    setMimeType(DicomMime.KO_DICOM);
  }
}
