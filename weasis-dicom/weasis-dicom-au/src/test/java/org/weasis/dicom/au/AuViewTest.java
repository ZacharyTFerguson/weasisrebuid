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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.sound.sampled.AudioFormat;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.codec.DicomSpecialElement;

class AuViewTest {

  @Test
  void eightBitMonoPcmAndPlayPauseStop() {
    Attributes au = voiceUb(8000.0, new byte[] {(byte) 128, (byte) 255, 0, (byte) 128});
    AuView view = new AuView();
    view.display(au);
    assertEquals(1, view.channelCount());
    assertEquals(4, view.sampleCount());
    assertEquals(8000.0, view.samplingFrequency());
    assertEquals(8, view.bitsAllocated());
    assertEquals(128, view.rawSample(0, 0));
    assertEquals(255, view.rawSample(0, 1));
    assertEquals(0, view.rawSample(0, 2));
    assertEquals(0.0, view.pcm(0, 0), 1e-9);
    assertEquals(127 / 128.0, view.pcm(0, 1), 1e-9);
    assertEquals(-1.0, view.pcm(0, 2), 1e-9);
    assertEquals(4 / 8000.0, view.durationSeconds(), 1e-12);
    AudioFormat format = view.audioFormat();
    assertEquals(AudioFormat.Encoding.PCM_UNSIGNED, format.getEncoding());
    assertEquals(8000f, format.getSampleRate());
    assertFalse(view.isPlaying());
    view.play();
    assertTrue(view.isPlaying());
    view.seek(2);
    assertEquals(2, view.position());
    view.pause();
    assertFalse(view.isPlaying());
    assertEquals(2, view.position());
    view.stop();
    assertEquals(0, view.position());
    new AuToolBar(view).play();
    assertTrue(view.isPlaying());
  }

  @Test
  void sixteenBitStereoInterleavedAndContainer() {
    byte[] data = new byte[] {0x00, 0x40, 0x00, (byte) 0xC0, 0x00, 0x20, 0x00, 0x10};
    Attributes au = voiceSs(2, 2, 11025.0, data);
    AuView view = new AuView();
    view.display(au);
    assertEquals(2, view.channelCount());
    assertEquals(2, view.sampleCount());
    assertEquals(0x4000, view.rawSample(0, 0));
    assertEquals((short) 0xC000, view.rawSample(1, 0));
    assertEquals(0x2000, view.rawSample(0, 1));
    assertEquals(0.5, view.pcm(0, 0), 1e-9);
    assertEquals(AudioFormat.Encoding.PCM_SIGNED, view.audioFormat().getEncoding());

    DicomMediaIO io = new DicomMediaIO(au, UID.ExplicitVRLittleEndian);
    assertInstanceOf(DicomSpecialElement.class, io.getPreview());
    Series<MediaElement> series = new Series<>();
    series.setMimeType(DicomMime.AU_DICOM);
    series.addMedia(io.getPreview());
    AuContainer container = new AuContainer();
    container.addSeries(series);
    assertEquals(2, container.getAuView().channelCount());
    assertEquals(11025.0, container.getAuView().samplingFrequency());
    assertInstanceOf(DicomAudioElement.class, new AuFactory().buildInstance(io));
  }

  static Attributes voiceUb(double hz, byte[] samples) {
    return waveform(1, samples.length, hz, 8, "UB", samples);
  }

  static Attributes voiceSs(int channels, int samples, double hz, byte[] data) {
    return waveform(channels, samples, hz, 16, "SS", data);
  }

  static Attributes waveform(
      int channels, int samples, double hz, int bits, String interp, byte[] data) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, UID.BasicVoiceAudioWaveformStorage);
    dcm.setString(Tag.Modality, VR.CS, "AU");
    Sequence seq = dcm.newSequence(Tag.WaveformSequence, 1);
    Attributes mux = new Attributes();
    mux.setInt(Tag.NumberOfWaveformChannels, VR.US, channels);
    mux.setInt(Tag.NumberOfWaveformSamples, VR.UL, samples);
    mux.setDouble(Tag.SamplingFrequency, VR.DS, hz);
    mux.setInt(Tag.WaveformBitsAllocated, VR.US, bits);
    mux.setString(Tag.WaveformSampleInterpretation, VR.CS, interp);
    mux.setBytes(Tag.WaveformData, bits <= 8 ? VR.OB : VR.OW, data);
    seq.add(mux);
    return dcm;
  }
}
