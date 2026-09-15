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

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.swing.JPanel;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.dicom.codec.DicomElement;

/**
 * Decodes a DICOM audio Waveform Sequence into interleaved PCM and tracks play/pause/stop without
 * opening a headed {@code SourceDataLine}.
 */
public class AuView extends JPanel {

  private int channelCount;
  private int sampleCount;
  private int bitsAllocated = 8;
  private double samplingFrequency;
  private int[] raw = new int[0];
  private boolean playing;
  private int position;

  public static Attributes multiplex(Attributes dataset) {
    if (dataset == null) {
      return new Attributes();
    }
    Sequence seq = dataset.getSequence(Tag.WaveformSequence);
    return seq != null && !seq.isEmpty() ? seq.get(0) : dataset;
  }

  public void display(Attributes dataset) {
    Attributes mux = multiplex(dataset);
    this.channelCount = Math.max(0, mux.getInt(Tag.NumberOfWaveformChannels, 0));
    this.sampleCount = Math.max(0, mux.getInt(Tag.NumberOfWaveformSamples, 0));
    this.samplingFrequency = mux.getDouble(Tag.SamplingFrequency, 0);
    this.bitsAllocated = mux.getInt(Tag.WaveformBitsAllocated, 8);
    String interp = mux.getString(Tag.WaveformSampleInterpretation, "UB");
    byte[] data;
    try {
      data = mux.getBytes(Tag.WaveformData);
    } catch (IOException e) {
      data = new byte[0];
    }
    if (data == null) {
      data = new byte[0];
    }
    boolean eightBit =
        bitsAllocated <= 8
            || "UB".equalsIgnoreCase(interp)
            || "MB".equalsIgnoreCase(interp)
            || "AB".equalsIgnoreCase(interp);
    if (eightBit) {
      bitsAllocated = 8;
    } else {
      bitsAllocated = 16;
    }
    this.raw = decodeRaw(data, eightBit);
    stop();
  }

  public void display(MediaElement media) {
    if (media instanceof DicomElement dicom) {
      display(dicom.getDicomObject());
    } else {
      display((Attributes) null);
    }
  }

  private int[] decodeRaw(byte[] data, boolean eightBit) {
    int n = channelCount * sampleCount;
    int[] out = new int[Math.max(0, n)];
    if (eightBit) {
      for (int i = 0; i < out.length && i < data.length; i++) {
        out[i] = data[i] & 0xff;
      }
      return out;
    }
    for (int i = 0; i < out.length; i++) {
      int index = i * 2;
      if (index + 1 >= data.length) {
        break;
      }
      out[i] = (short) ((data[index] & 0xff) | (data[index + 1] << 8));
    }
    return out;
  }

  public int channelCount() {
    return channelCount;
  }

  public int sampleCount() {
    return sampleCount;
  }

  public int bitsAllocated() {
    return bitsAllocated;
  }

  public double samplingFrequency() {
    return samplingFrequency;
  }

  public double durationSeconds() {
    if (samplingFrequency <= 0) {
      return 0;
    }
    return sampleCount / samplingFrequency;
  }

  public int rawSample(int channel, int sample) {
    if (channel < 0 || sample < 0 || channel >= channelCount || sample >= sampleCount) {
      return 0;
    }
    int index = sample * channelCount + channel;
    if (index < 0 || index >= raw.length) {
      return 0;
    }
    return raw[index];
  }

  /** PCM in [-1, 1]. UB is centered at 128; SS is divided by 32768. */
  public double pcm(int channel, int sample) {
    int v = rawSample(channel, sample);
    if (bitsAllocated <= 8) {
      return (v - 128) / 128.0;
    }
    return v / 32768.0;
  }

  public AudioFormat audioFormat() {
    boolean signed = bitsAllocated > 8;
    int frameSize = Math.max(1, channelCount) * (bitsAllocated / 8);
    float hz = samplingFrequency <= 0 ? 8000f : (float) samplingFrequency;
    return new AudioFormat(
        signed ? AudioFormat.Encoding.PCM_SIGNED : AudioFormat.Encoding.PCM_UNSIGNED,
        hz,
        bitsAllocated,
        Math.max(1, channelCount),
        Math.max(1, frameSize),
        hz,
        false);
  }

  public void play() {
    if (sampleCount > 0) {
      playing = true;
    }
  }

  public void pause() {
    playing = false;
  }

  public void stop() {
    playing = false;
    position = 0;
  }

  public boolean isPlaying() {
    return playing;
  }

  public int position() {
    return position;
  }

  public void seek(int sample) {
    if (sampleCount <= 0) {
      position = 0;
      return;
    }
    position = Math.max(0, Math.min(sampleCount - 1, sample));
  }
}
