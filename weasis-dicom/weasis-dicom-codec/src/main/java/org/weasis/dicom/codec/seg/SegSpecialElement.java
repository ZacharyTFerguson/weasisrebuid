/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.dicom.codec.seg;

import org.weasis.dicom.codec.DcmMediaReader;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.codec.DicomSpecialElement;

public class SegSpecialElement extends DicomSpecialElement {
  private final SegmentationVolume volume = new SegmentationVolume();
  private final SegVisibilityPolicy visibility = new SegVisibilityPolicy();

  public SegSpecialElement(DcmMediaReader mediaIO) {
    super(mediaIO);
    setMimeType(DicomMime.SEG_DICOM);
  }

  public SegmentationVolume getVolume() {
    return volume;
  }

  public SegVisibilityPolicy getVisibility() {
    return visibility;
  }
}
