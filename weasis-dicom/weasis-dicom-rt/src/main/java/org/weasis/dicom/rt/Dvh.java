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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;

/** One DVH Sequence item. */
public class Dvh {

  private final String type;
  private final double minDose;
  private final double maxDose;
  private final double meanDose;
  private final double[] data;

  public Dvh(String type, double minDose, double maxDose, double meanDose, double[] data) {
    this.type = type == null ? "" : type;
    this.minDose = minDose;
    this.maxDose = maxDose;
    this.meanDose = meanDose;
    this.data = data == null ? new double[0] : data.clone();
  }

  public static Dvh from(Attributes item) {
    Attributes src = item == null ? new Attributes() : item;
    return new Dvh(
        src.getString(Tag.DVHType, ""),
        src.getDouble(Tag.DVHMinimumDose, 0),
        src.getDouble(Tag.DVHMaximumDose, 0),
        src.getDouble(Tag.DVHMeanDose, 0),
        src.getDoubles(Tag.DVHData));
  }

  public String type() {
    return type;
  }

  public double minDose() {
    return minDose;
  }

  public double maxDose() {
    return maxDose;
  }

  public double meanDose() {
    return meanDose;
  }

  public double[] data() {
    return data.clone();
  }
}
