/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import org.weasis.core.ui.editor.image.ImageViewerEventManager;

/** DICOM 2D event manager (Weasis type name). Applies W/L on the bound {@link View2d}. */
public class EventManager extends ImageViewerEventManager {

  private final View2d view2d;

  public EventManager(View2d view) {
    super(view);
    this.view2d = view;
  }

  public View2d getView2d() {
    return view2d;
  }

  @Override
  protected void applyWindowLevel(int dx, int dy) {
    view2d.setWindowLevel(view2d.getWindow() + dx, view2d.getLevel() - dy);
  }
}
