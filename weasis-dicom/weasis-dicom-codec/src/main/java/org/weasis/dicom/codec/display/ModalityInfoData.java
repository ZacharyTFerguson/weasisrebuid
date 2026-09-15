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

public class ModalityInfoData {
  private final Modality modality;
  private final CornerInfoData cornerInfo = new CornerInfoData();

  public ModalityInfoData(Modality modality) {
    this.modality = modality == null ? Modality.DEFAULT : modality;
  }

  public Modality getModality() {
    return modality;
  }

  public CornerInfoData getCornerInfo() {
    return cornerInfo;
  }
}
