/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.wado;

/** Per-series retrieve progress (downloaded vs listed instances). */
public class DicomSeriesProgressMonitor {

  private int total;
  private int done;

  public void setTotal(int total) {
    this.total = Math.max(0, total);
  }

  public int total() {
    return total;
  }

  public int done() {
    return done;
  }

  public void increment() {
    done++;
  }

  public double ratio() {
    if (total <= 0) {
      return 0.0;
    }
    return Math.min(1.0, (double) done / (double) total);
  }
}
