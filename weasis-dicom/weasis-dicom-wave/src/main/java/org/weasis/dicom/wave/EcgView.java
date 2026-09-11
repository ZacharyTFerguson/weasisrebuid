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

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

public class EcgView extends ViewerPlugin<MediaElement> {

  public enum Layout {
    L12x1,
    L6x2,
    L3x4_RHYTHM
  }

  public enum Scale {
    AUTO,
    MANUAL
  }

  private Layout layout = Layout.L12x1;
  private Scale timeScale = Scale.AUTO;
  private Scale voltageScale = Scale.AUTO;
  private Double start;
  private Double end;

  public EcgView() {
    super("DICOM ECG");
  }

  public Layout displayLayout() {
    return layout;
  }

  public void setDisplayLayout(Layout layout) {
    this.layout = layout;
  }

  public Scale timeScale() {
    return timeScale;
  }

  public Scale voltageScale() {
    return voltageScale;
  }

  /** Click = start; Ctrl/right = end; middle/Shift = delete. One measurement per lead. */
  public void click(boolean ctrlOrRight, boolean middleOrShift, double t) {
    if (middleOrShift) {
      start = null;
      end = null;
      return;
    }
    if (ctrlOrRight) {
      end = t;
    } else {
      start = t;
      end = null;
    }
  }

  public Double start() {
    return start;
  }

  public Double end() {
    return end;
  }

  public double duration() {
    if (start == null || end == null) {
      return 0;
    }
    return end - start;
  }
}
