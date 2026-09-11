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

import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Dedicated AU player: play/pause, scrub in seconds, volume independent of the system mixer. */
public class AuPlayer extends ViewerPlugin<MediaElement> {

  private boolean playing;
  private double positionSeconds;
  private double volume = 1.0;

  public AuPlayer() {
    super("DICOM AU");
  }

  public void play() {
    playing = true;
  }

  public void pause() {
    playing = false;
  }

  public boolean playing() {
    return playing;
  }

  public void scrub(double seconds) {
    positionSeconds = Math.max(0, seconds);
  }

  public double positionSeconds() {
    return positionSeconds;
  }

  public void setVolume(double volume) {
    this.volume = Math.max(0, Math.min(1, volume));
  }

  public double volume() {
    return volume;
  }

  public enum ExportFormat {
    AU_AS_IS,
    WAVE
  }
}
