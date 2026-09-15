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
import org.dcm4che3.data.Tag;
import org.weasis.core.api.image.util.WindLevelParameters;
import org.weasis.dicom.codec.utils.DicomMediaUtils;

/** Reads GSPS VOI / graphic layers from a PR dataset. */
public class PresentationStateReader {
  private final PRSpecialElement pr;

  public PresentationStateReader(PRSpecialElement pr) {
    this.pr = pr;
  }

  public PRSpecialElement getPr() {
    return pr;
  }

  public Attributes getDicomObject() {
    return pr == null ? null : pr.getDicomObject();
  }

  public WindLevelParameters getWindowLevel(double defaultWindow, double defaultLevel) {
    return DicomMediaUtils.windowLevel(getDicomObject(), defaultWindow, defaultLevel);
  }

  public String getPresentationLabel() {
    Attributes dcm = getDicomObject();
    if (dcm == null) {
      return "";
    }
    String label = dcm.getString(Tag.ContentLabel);
    return label == null ? "" : label;
  }
}
