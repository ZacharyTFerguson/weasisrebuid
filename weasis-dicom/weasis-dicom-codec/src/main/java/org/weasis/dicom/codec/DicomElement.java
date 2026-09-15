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

import org.dcm4che3.data.Attributes;

/** One DICOM instance (image, KO, PR, video, encapsulated doc). */
public interface DicomElement {

  DcmMediaReader getMediaReader();

  default Attributes getDicomObject() {
    DcmMediaReader reader = getMediaReader();
    return reader == null ? null : reader.getDicomObject();
  }

  String getKey();
}
