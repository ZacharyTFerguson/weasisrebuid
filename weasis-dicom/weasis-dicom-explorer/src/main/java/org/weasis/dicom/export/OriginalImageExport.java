/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.export;

/** MX-05: Preserve 16-bit ON → Modality LUT / HU. Header VOI only when 16-bit is OFF. */
public final class OriginalImageExport {

  private boolean preserve16Bit = true;

  public void setPreserve16Bit(boolean preserve16Bit) {
    this.preserve16Bit = preserve16Bit;
  }

  public boolean preserve16Bit() {
    return preserve16Bit;
  }

  public boolean usesModalityLutHu() {
    return preserve16Bit;
  }

  public boolean usesHeaderVoi() {
    return !preserve16Bit;
  }
}
