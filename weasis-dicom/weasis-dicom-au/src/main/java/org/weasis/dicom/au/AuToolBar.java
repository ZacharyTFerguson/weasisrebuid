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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/** Play / pause / stop chrome for {@link AuView}. */
public class AuToolBar extends WtoolBar {

  public static final String NAME = "Audio";
  private final AuView view;

  public AuToolBar(AuView view) {
    super(NAME, 10);
    this.view = view == null ? new AuView() : view;
    add(control("Play", "play", this::play));
    add(control("Pause", "pause", this::pause));
    add(control("Stop", "stop", this::stop));
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

  JButton control(String text, String name, Runnable action) {
    JButton button =
        new JButton(
            new AbstractAction(text) {
              @Override
              public void actionPerformed(ActionEvent e) {
                action.run();
              }
            });
    button.setName(name);
    return button;
  }
}
