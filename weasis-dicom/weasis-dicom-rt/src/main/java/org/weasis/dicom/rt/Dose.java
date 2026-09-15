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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** RT Dose: grid scaling and DVH items. */
public class Dose {

  private final double gridScaling;
  private final List<Dvh> dvhs;

  public Dose(double gridScaling, List<Dvh> dvhs) {
    this.gridScaling = gridScaling;
    this.dvhs = dvhs == null ? List.of() : List.copyOf(dvhs);
  }

  public static Dose from(Attributes dataset) {
    if (dataset == null) {
      return new Dose(1.0, List.of());
    }
    List<Dvh> list = new ArrayList<>();
    Sequence seq = dataset.getSequence(Tag.DVHSequence);
    if (seq != null) {
      for (Attributes item : seq) {
        list.add(Dvh.from(item));
      }
    }
    return new Dose(dataset.getDouble(Tag.DoseGridScaling, 1.0), list);
  }

  public double gridScaling() {
    return gridScaling;
  }

  public List<Dvh> dvhs() {
    return Collections.unmodifiableList(dvhs);
  }
}
