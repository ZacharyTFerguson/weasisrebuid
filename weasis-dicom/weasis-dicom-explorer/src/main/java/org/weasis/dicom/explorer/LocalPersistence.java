/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

/** Process-wide DICOM explorer model so File &gt; Import and the explorer share one tree. */
public class LocalPersistence {

  private static DicomModel model = new DicomModel();

  private LocalPersistence() {}

  public static synchronized DicomModel getDicomModel() {
    if (model == null) {
      model = new DicomModel();
    }
    return model;
  }

  public static synchronized void reset() {
    model = new DicomModel();
  }
}
