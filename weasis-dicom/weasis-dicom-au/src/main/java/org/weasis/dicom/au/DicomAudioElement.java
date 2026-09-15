/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.au;

import org.weasis.dicom.codec.DcmMediaReader;
import org.weasis.dicom.codec.DicomSpecialElement;

/** AU waveform instance bound to the DICOM Audio Player. */
public class DicomAudioElement extends DicomSpecialElement {

  public DicomAudioElement(DcmMediaReader mediaIO) {
    super(mediaIO);
  }
}
