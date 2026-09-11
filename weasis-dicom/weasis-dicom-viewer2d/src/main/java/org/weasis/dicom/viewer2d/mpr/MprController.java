/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Owns axes, MIP, and the three orthogonal panes. Plane coupling cannot be turned off. Fusion
 * broadcast is WP-8.
 */
public final class MprController {

  private final MprVolume geometry;
  private final Volume volume;
  private final AxesControl axes;
  private final MipProjector mip = new MipProjector();
  private final MprPrefView prefs = new MprPrefView();
  private final List<MprView> views = new ArrayList<>();
  private boolean scrollSync = true;
  private boolean zoomSync = true;
  private boolean wlSync = true;
  private boolean panSync;
  private boolean rotationSync;

  public MprController(MprVolume geometry, Volume volume) {
    this.geometry = geometry;
    this.volume = volume;
    this.axes = new AxesControl(geometry);
    views.add(new MprView(Plane.AXIAL));
    views.add(new MprView(Plane.CORONAL));
    views.add(new MprView(Plane.SAGITTAL));
  }

  public MprVolume geometry() {
    return geometry;
  }

  public Volume volume() {
    return volume;
  }

  public AxesControl axes() {
    return axes;
  }

  public MipProjector mip() {
    return mip;
  }

  public MprPrefView prefs() {
    return prefs;
  }

  public List<MprView> views() {
    return List.copyOf(views);
  }

  public boolean couplingAlwaysOn() {
    return axes.couplingAlwaysOn();
  }

  public boolean scrollSync() {
    return scrollSync;
  }

  public boolean zoomSync() {
    return zoomSync;
  }

  public boolean wlSync() {
    return wlSync;
  }

  public boolean panSync() {
    return panSync;
  }

  public boolean rotationSync() {
    return rotationSync;
  }

  public MprView view(Plane plane) {
    for (MprView v : views) {
      if (v.plane() == plane) {
        return v;
      }
    }
    return views.get(0);
  }
}
