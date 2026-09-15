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

/** Dataset wrapper for RT SOP instances. */
public class DataSource {

  private final Attributes dataset;

  public DataSource(Attributes dataset) {
    this.dataset = dataset;
  }

  public Attributes dataset() {
    return dataset;
  }

  public String modality() {
    return dataset == null ? "" : dataset.getString(Tag.Modality, "");
  }

  public String sopClassUid() {
    return dataset == null ? "" : dataset.getString(Tag.SOPClassUID, "");
  }
}
