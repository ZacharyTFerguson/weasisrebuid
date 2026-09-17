/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.Color;

/** Overlay cine Play / Stop control. */
public class PlayViewButton extends ViewButton {

  public static final String PLAY = "Play";
  public static final String STOP = "Stop";

  public PlayViewButton() {
    super(PLAY, new AreaIcon(Color.GREEN));
    setName("play");
    setOverlay(4, 4);
  }

  @Override
  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.toggleCine();
    sync(view);
  }

  public void sync(DefaultView2d<?> view) {
    boolean running = running(view);
    setSelected(running);
    setText(running ? STOP : PLAY);
    setIcon(new AreaIcon(running ? Color.RED : Color.GREEN));
  }

  static boolean running(DefaultView2d<?> view) {
    return view != null && view.cineListener().isCineRunning();
  }
}
