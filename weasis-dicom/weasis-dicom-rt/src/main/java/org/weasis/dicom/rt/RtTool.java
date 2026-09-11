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

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

public class RtTool extends ViewerPlugin<MediaElement> {

  public static final String DVH_RECALC = "weasis.rt.dvh.recalculate.enable";
  public static final double FILL_OPACITY = 0.20;
  public static final double ISODOSE_GRAPHIC_OPACITY = 0.50;

  private boolean isodoseRootChecked;
  private boolean loadRtEnabled = true;
  private boolean dvhRecalcEnabled = true;

  public RtTool() {
    super("DICOM RT");
  }

  public boolean isodoseRootCheckedByDefault() {
    return isodoseRootChecked;
  }

  public void loadRt() {
    loadRtEnabled = false;
  }

  public boolean loadRtEnabled() {
    return loadRtEnabled;
  }

  public void setDvhRecalcEnabled(boolean dvhRecalcEnabled) {
    this.dvhRecalcEnabled = dvhRecalcEnabled;
  }

  public boolean dvhRecalcEnabled() {
    return dvhRecalcEnabled;
  }

  public enum Sop {
    RTSTRUCT,
    RTDOSE,
    RTPLAN
  }
}
