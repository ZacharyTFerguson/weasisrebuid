/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;

class SuvFactorTest {

  @Test
  void bqmlNoDecayIsWeightOverDose() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.Units, VR.CS, "BQML");
    dcm.setDouble(Tag.PatientWeight, VR.DS, 70.0);
    Sequence seq = dcm.newSequence(Tag.RadiopharmaceuticalInformationSequence, 1);
    Attributes radio = new Attributes();
    radio.setDouble(Tag.RadionuclideTotalDose, VR.DS, 70_000_000.0);
    seq.add(radio);
    assertEquals(0.001, SuvFactor.factor(dcm), 1e-9);
  }

  @Test
  void nonBqmlIsIdentity() {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.Units, VR.CS, "CNTS");
    assertEquals(1.0, SuvFactor.factor(dcm), 1e-9);
  }
}
