/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.cmpr;

import org.weasis.dicom.viewer2d.mpr.Volume;

/** Holds {@link CrossSectionParams} used to preview a curve-perpendicular plane. */
public class CrossSectionDialog {

  private CrossSectionParams params = new CrossSectionParams();

  public CrossSectionParams getParams() {
    return params;
  }

  public void setParams(CrossSectionParams params) {
    this.params = params == null ? new CrossSectionParams() : params;
  }

  public double[][] preview(Volume volume) {
    return new CrossSectionImageIO().slice(volume, params);
  }
}
