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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

class WaveViewTest {

  @Test
  void sixteenBitSamplesConvertToMillivoltsAndTwoLeadLayout() {
    Attributes ecg =
        twoLeadSs(500.0, 0.005, Unit.MILLIVOLT, shorts(200, 400, 200, 400, 200, 400, 200, 400));
    WaveDataReadable data = WaveView.decode(ecg);
    assertInstanceOf(WaveShortData.class, data);
    assertEquals(2, data.channelCount());
    assertEquals(4, data.sampleCount());
    assertEquals(500.0, data.samplingFrequency());
    assertEquals(200, data.rawSample(0, 0));
    assertEquals(400, data.rawSample(1, 0));
    assertEquals(200, data.rawSample(0, 1));
    assertEquals(1.0, data.millivolt(0, 0), 1e-9);
    assertEquals(2.0, data.millivolt(1, 0), 1e-9);
    assertEquals(0.008, data.durationSeconds(), 1e-9);
    assertEquals(Lead.I, data.channelDefinitions().get(0).lead());
    assertEquals(Lead.II, data.channelDefinitions().get(1).lead());

    WaveView view = new WaveView();
    view.display(ecg);
    assertEquals(Format.TWO, view.format());
    assertEquals(2, view.leadPanels().size());
    assertEquals(Lead.I, view.leadPanels().get(0).lead());
    assertEquals(1.0, view.leadPanels().get(0).millivolts()[0], 1e-9);
    assertTrue(view.infoPanel().text().contains("500"));
    assertTrue(new DefaultPrinter().print(data).contains("I:1.0"));
  }

  @Test
  void eightBitAndMicrovoltsAndContainer() {
    Attributes ecg = twoLeadUb(250.0, 1000.0, Unit.MICROVOLT, new byte[] {1, 2, 1, 2});
    WaveDataReadable data = WaveView.decode(ecg);
    assertInstanceOf(WaveByteData.class, data);
    assertEquals(1, data.rawSample(0, 0));
    assertEquals(2, data.rawSample(1, 0));
    assertEquals(1.0, data.millivolt(0, 0), 1e-9);

    DicomMediaIO io = new DicomMediaIO(ecg, UID.ExplicitVRLittleEndian);
    assertInstanceOf(DicomSpecialElement.class, io.getPreview());
    Series<MediaElement> series = new Series<>();
    series.setMimeType(DicomMime.WAVE_DICOM);
    series.addMedia(io.getPreview());
    WaveContainer container = new WaveContainer();
    container.addSeries(series);
    assertEquals(2, container.getWaveView().data().channelCount());
    assertInstanceOf(DicomSpecialElement.class, new WaveFactory().buildInstance(io));
  }

  static Attributes twoLeadSs(double hz, double sensitivity, String unit, short[] interleaved) {
    byte[] bytes = new byte[interleaved.length * 2];
    for (int i = 0; i < interleaved.length; i++) {
      bytes[i * 2] = (byte) (interleaved[i] & 0xff);
      bytes[i * 2 + 1] = (byte) ((interleaved[i] >> 8) & 0xff);
    }
    return waveform(
        UID.TwelveLeadECGWaveformStorage,
        2,
        interleaved.length / 2,
        hz,
        16,
        "SS",
        bytes,
        sensitivity,
        unit);
  }

  static Attributes twoLeadUb(double hz, double sensitivity, String unit, byte[] interleaved) {
    return waveform(
        UID.GeneralECGWaveformStorage,
        2,
        interleaved.length / 2,
        hz,
        8,
        "UB",
        interleaved,
        sensitivity,
        unit);
  }

  static Attributes waveform(
      String sop,
      int channels,
      int samples,
      double hz,
      int bits,
      String interp,
      byte[] data,
      double sensitivity,
      String unit) {
    Attributes dcm = new Attributes();
    dcm.setString(Tag.SOPClassUID, VR.UI, sop);
    dcm.setString(Tag.Modality, VR.CS, "ECG");
    Sequence seq = dcm.newSequence(Tag.WaveformSequence, 1);
    Attributes mux = new Attributes();
    mux.setInt(Tag.NumberOfWaveformChannels, VR.US, channels);
    mux.setInt(Tag.NumberOfWaveformSamples, VR.UL, samples);
    mux.setDouble(Tag.SamplingFrequency, VR.DS, hz);
    mux.setInt(Tag.WaveformBitsAllocated, VR.US, bits);
    mux.setString(Tag.WaveformSampleInterpretation, VR.CS, interp);
    Sequence defs = mux.newSequence(Tag.ChannelDefinitionSequence, 2);
    defs.add(channel("I", sensitivity, unit));
    defs.add(channel("II", sensitivity, unit));
    mux.setBytes(Tag.WaveformData, bits <= 8 ? VR.OB : VR.OW, data);
    seq.add(mux);
    return dcm;
  }

  static Attributes channel(String label, double sensitivity, String unit) {
    Attributes item = new Attributes();
    item.setString(Tag.ChannelLabel, VR.SH, label);
    item.setDouble(Tag.ChannelSensitivity, VR.DS, sensitivity);
    item.setDouble(Tag.ChannelSensitivityCorrectionFactor, VR.DS, 1.0);
    item.setDouble(Tag.ChannelBaseline, VR.DS, 0);
    Sequence units = item.newSequence(Tag.ChannelSensitivityUnitsSequence, 1);
    Attributes code = new Attributes();
    code.setString(Tag.CodeMeaning, VR.LO, unit);
    units.add(code);
    return item;
  }

  static short[] shorts(int... values) {
    short[] out = new short[values.length];
    for (int i = 0; i < values.length; i++) {
      out[i] = (short) values[i];
    }
    return out;
  }
}
