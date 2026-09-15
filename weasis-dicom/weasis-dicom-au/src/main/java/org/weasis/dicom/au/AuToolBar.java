/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.au;

import javax.swing.JButton;
import javax.swing.JToolBar;

/** Play / pause / stop chrome for {@link AuView}. */
public class AuToolBar extends JToolBar {

  private final AuView view;

  public AuToolBar(AuView view) {
    this.view = view == null ? new AuView() : view;
    JButton play = new JButton("Play");
    play.addActionListener(e -> play());
    JButton pause = new JButton("Pause");
    pause.addActionListener(e -> pause());
    JButton stop = new JButton("Stop");
    stop.addActionListener(e -> stop());
    add(play);
    add(pause);
    add(stop);
  }

  public AuView getView() {
    return view;
  }

  public void play() {
    view.play();
  }

  public void pause() {
    view.pause();
  }

  public void stop() {
    view.stop();
  }
}
