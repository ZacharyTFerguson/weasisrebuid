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

import java.awt.GraphicsEnvironment;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.Timer;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.api.gui.util.SliderCineListener;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.util.Toolbar;

/** Cine chrome. Play/Stop toggle {@link ActionW#CINE}; Have tests call {@link #tick()}. */
public class CineToolBar implements Toolbar {

  public static final String NAME = "Cine";
  private final JToolBar bar = new JToolBar(NAME);
  private final JButton play = new JButton("Play");
  private final JButton stop = new JButton("Stop");
  private final SliderCineListener cine;
  private int position = 35;
  private boolean enabled = true;
  private boolean playing;
  private boolean clockEnabled = !GraphicsEnvironment.isHeadless();
  private Timer clock;
  private DefaultView2d<?> view;

  public CineToolBar() {
    cine =
        new SliderCineListener(ActionW.CINE, 0, 0, 0) {
          @Override
          public void stateChanged(int value) {
            if (view != null) {
              view.setFrameIndex(value);
            }
          }
        };
    bar.add(play);
    bar.add(stop);
    play.setName("cine-play");
    stop.setName("cine-stop");
    play.addActionListener(e -> play());
    stop.addActionListener(e -> stop());
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    int max = view == null ? 0 : Math.max(0, view.getFrameCount() - 1);
    cine.getSlider().setMinimum(0);
    cine.getSlider().setMaximum(max);
    cine.setSliderValue(view == null ? 0 : view.getFrameIndex());
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public SliderCineListener cine() {
    return cine;
  }

  public void play() {
    playing = true;
    cine.start();
    startClock();
  }

  public void stop() {
    playing = false;
    cine.stop();
    if (clock != null) {
      clock.stop();
    }
  }

  public boolean isPlaying() {
    return playing;
  }

  public void setPlaying(boolean playing) {
    if (playing) {
      play();
    } else {
      stop();
    }
  }

  public boolean isClockEnabled() {
    return clockEnabled;
  }

  public void setClockEnabled(boolean clockEnabled) {
    this.clockEnabled = clockEnabled;
    if (!clockEnabled && clock != null) {
      clock.stop();
    }
  }

  /** One cine step. Have tests use this instead of waiting on the Swing timer. */
  public void tick() {
    cine.tick();
  }

  void startClock() {
    if (!clockEnabled) {
      return;
    }
    if (clock == null) {
      clock = new Timer(cine.millisPerFrame(), e -> cine.tick());
    }
    clock.setDelay(cine.millisPerFrame());
    clock.start();
  }

  @Override
  public JComponent getComponent() {
    return bar;
  }

  @Override
  public String getComponentName() {
    return NAME;
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOLBAR;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return enabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
