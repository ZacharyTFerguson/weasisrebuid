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

import org.dcm4che3.data.Attributes;

/** 16-bit signed little-endian (SS) multiplexed waveform samples. */
public class WaveShortData extends AbstractWaveData {

  public WaveShortData(Attributes multiplex) {
    super(multiplex);
  }

  @Override
  public int rawSample(int channel, int sample) {
    if (channel < 0 || sample < 0 || channel >= channelCount() || sample >= sampleCount()) {
      return 0;
    }
    int index = interleavedIndex(channel, sample) * 2;
    byte[] data = waveformData();
    if (index + 1 >= data.length) {
      return 0;
    }
    return (short) ((data[index] & 0xff) | (data[index + 1] << 8));
  }
}
