/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Ordered leads for one ECG page. */
public class WaveLayout {

  private final Format format;
  private final List<Lead> leads;

  public WaveLayout(Format format, List<Lead> leads) {
    this.format = format == null ? Format.DEFAULT : format;
    this.leads = leads == null ? List.of() : List.copyOf(leads);
  }

  public Format format() {
    return format;
  }

  public List<Lead> leads() {
    return Collections.unmodifiableList(leads);
  }

  public int rows() {
    return format.rows();
  }

  public int columns() {
    return format.columns();
  }

  public static List<Lead> twelveLeadOrder() {
    List<Lead> list = new ArrayList<>();
    list.add(Lead.I);
    list.add(Lead.AVR);
    list.add(Lead.V1);
    list.add(Lead.V4);
    list.add(Lead.II);
    list.add(Lead.AVL);
    list.add(Lead.V2);
    list.add(Lead.V5);
    list.add(Lead.III);
    list.add(Lead.AVF);
    list.add(Lead.V3);
    list.add(Lead.V6);
    return list;
  }
}
