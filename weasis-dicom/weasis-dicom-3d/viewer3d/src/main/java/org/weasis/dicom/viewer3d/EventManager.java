/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer3d;

import org.weasis.dicom.viewer3d.vr.View3d;

/** Focused 3D view and current volume action. */
public final class EventManager {

  private static final EventManager INSTANCE = new EventManager();

  private View3d selectedView;
  private ActionVol action = ActionVol.RENDERING_TYPE;

  private EventManager() {}

  public static EventManager getInstance() {
    return INSTANCE;
  }

  public View3d getSelectedView() {
    return selectedView;
  }

  public void setSelectedView(View3d selectedView) {
    this.selectedView = selectedView;
  }

  public ActionVol getAction() {
    return action;
  }

  public void setAction(ActionVol action) {
    if (action != null) {
      this.action = action;
    }
  }
}
