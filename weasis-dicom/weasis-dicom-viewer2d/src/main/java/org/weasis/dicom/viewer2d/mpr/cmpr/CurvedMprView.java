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

import org.weasis.dicom.viewer2d.mpr.MprView;
import org.weasis.dicom.viewer2d.mpr.Volume;

/** Hosts a straightened CPR image rebuilt from {@link CurvedMprAxis}. */
public class CurvedMprView extends MprView {

  private CurvedMprAxis curvedAxis = new CurvedMprAxis();

  public CurvedMprAxis getCurvedAxis() {
    return curvedAxis;
  }

  public void setCurvedAxis(CurvedMprAxis curvedAxis) {
    this.curvedAxis = curvedAxis == null ? new CurvedMprAxis() : curvedAxis;
  }

  @Override
  public double[][] rebuild(Volume volume) {
    return new CurvedMprImageIO().read(volume, curvedAxis);
  }
}
