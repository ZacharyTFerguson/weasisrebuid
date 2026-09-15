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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;

/** Shared multiplex-group metadata and millivolt conversion. */
public abstract class AbstractWaveData implements WaveDataReadable {

  private final int channelCount;
  private final int sampleCount;
  private final double samplingFrequency;
  private final List<ChannelDefinition> definitions;
  private final byte[] waveformData;

  protected AbstractWaveData(Attributes multiplex) {
    Attributes mux = multiplex == null ? new Attributes() : multiplex;
    this.channelCount = Math.max(0, mux.getInt(Tag.NumberOfWaveformChannels, 0));
    this.sampleCount = Math.max(0, mux.getInt(Tag.NumberOfWaveformSamples, 0));
    this.samplingFrequency = mux.getDouble(Tag.SamplingFrequency, 0);
    byte[] raw;
    try {
      raw = mux.getBytes(Tag.WaveformData);
    } catch (IOException e) {
      raw = new byte[0];
    }
    this.waveformData = raw == null ? new byte[0] : raw;
    this.definitions = parseChannels(mux, channelCount);
  }

  static List<ChannelDefinition> parseChannels(Attributes mux, int channelCount) {
    List<ChannelDefinition> list = new ArrayList<>();
    Sequence seq = mux.getSequence(Tag.ChannelDefinitionSequence);
    int n = seq == null ? 0 : seq.size();
    for (int i = 0; i < channelCount; i++) {
      Attributes item = i < n && seq != null ? seq.get(i) : new Attributes();
      list.add(ChannelDefinition.from(item, i));
    }
    return list;
  }

  protected byte[] waveformData() {
    return waveformData;
  }

  @Override
  public int channelCount() {
    return channelCount;
  }

  @Override
  public int sampleCount() {
    return sampleCount;
  }

  @Override
  public double samplingFrequency() {
    return samplingFrequency;
  }

  @Override
  public List<ChannelDefinition> channelDefinitions() {
    return Collections.unmodifiableList(definitions);
  }

  @Override
  public double millivolt(int channel, int sample) {
    if (channel < 0 || channel >= definitions.size()) {
      return 0;
    }
    return definitions.get(channel).toMillivolt(rawSample(channel, sample));
  }

  protected int interleavedIndex(int channel, int sample) {
    return sample * channelCount + channel;
  }
}
