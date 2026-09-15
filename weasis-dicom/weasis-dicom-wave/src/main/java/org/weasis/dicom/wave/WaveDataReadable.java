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

import java.util.List;

/** Decoded multiplexed waveform samples (DICOM Waveform Module). */
public interface WaveDataReadable {

  int channelCount();

  int sampleCount();

  double samplingFrequency();

  int rawSample(int channel, int sample);

  double millivolt(int channel, int sample);

  List<ChannelDefinition> channelDefinitions();

  default double durationSeconds() {
    double freq = samplingFrequency();
    return freq <= 0 ? 0 : sampleCount() / freq;
  }
}
