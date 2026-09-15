/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

import java.awt.Color;

/** Display color for an isodose percent. */
public class DoseLut {

  public Color colorForPercent(double percent) {
    if (percent >= 95) {
      return Color.RED;
    }
    if (percent >= 70) {
      return Color.ORANGE;
    }
    if (percent >= 50) {
      return Color.YELLOW;
    }
    return Color.CYAN;
  }
}
