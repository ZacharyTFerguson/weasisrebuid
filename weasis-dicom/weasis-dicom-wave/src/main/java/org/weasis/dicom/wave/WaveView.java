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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.dicom.codec.DicomElement;

/** Displays multiplexed ECG waveform samples as millivolt lead traces. */
public class WaveView extends JPanel {

  private final WaveLayoutManager layoutManager = new WaveLayoutManager();
  private final InfoPanel infoPanel = new InfoPanel();
  private final JPanel leadGrid = new JPanel();
  private final List<LeadPanel> leadPanels = new ArrayList<>();
  private final ToolPanel toolPanel;
  private WaveDataReadable data;
  private Format format = Format.DEFAULT;
  private WaveLayout layout = new WaveLayout(Format.DEFAULT, List.of());

  public WaveView() {
    super(new BorderLayout());
    this.toolPanel = new ToolPanel(this);
    JPanel north = new JPanel(new BorderLayout());
    north.add(toolPanel, BorderLayout.CENTER);
    north.add(infoPanel, BorderLayout.SOUTH);
    add(north, BorderLayout.NORTH);
    add(leadGrid, BorderLayout.CENTER);
  }

  public static WaveDataReadable decode(Attributes dataset) {
    if (dataset == null) {
      return new WaveShortData(new Attributes());
    }
    Sequence seq = dataset.getSequence(Tag.WaveformSequence);
    Attributes mux = seq != null && !seq.isEmpty() ? seq.get(0) : dataset;
    int bits = mux.getInt(Tag.WaveformBitsAllocated, 16);
    String interp = mux.getString(Tag.WaveformSampleInterpretation, "SS");
    if (bits <= 8 || "UB".equalsIgnoreCase(interp) || "MB".equalsIgnoreCase(interp)) {
      return new WaveByteData(mux);
    }
    return new WaveShortData(mux);
  }

  public void display(Attributes dataset) {
    this.data = decode(dataset);
    setFormat(Format.forChannelCount(data.channelCount()));
  }

  public void display(MediaElement media) {
    if (media instanceof DicomElement dicom) {
      display(dicom.getDicomObject());
    } else {
      display((Attributes) null);
    }
  }

  public void setFormat(Format format) {
    this.format = format == null ? Format.DEFAULT : format;
    if (toolPanel.format() != this.format) {
      toolPanel.setFormat(this.format);
    }
    rebuild();
  }

  public Format format() {
    return format;
  }

  public WaveDataReadable data() {
    return data;
  }

  public WaveLayout waveLayout() {
    return layout;
  }

  public InfoPanel infoPanel() {
    return infoPanel;
  }

  public List<LeadPanel> leadPanels() {
    return Collections.unmodifiableList(leadPanels);
  }

  void rebuild() {
    leadPanels.clear();
    leadGrid.removeAll();
    if (data == null || data.channelCount() == 0) {
      layout = layoutManager.layout(format, List.of());
      infoPanel.setWaveform(data);
      revalidate();
      return;
    }
    layout = layoutManager.layout(format, data.channelDefinitions());
    leadGrid.setLayout(new GridLayout(layout.rows(), layout.columns()));
    for (Lead lead : layout.leads()) {
      int channel = channelIndex(lead);
      double[] mv = millivolts(channel);
      LeadPanel panel = new LeadPanel(lead, mv, data.samplingFrequency());
      leadPanels.add(panel);
      leadGrid.add(panel);
    }
    infoPanel.setWaveform(data);
    revalidate();
  }

  int channelIndex(Lead lead) {
    List<ChannelDefinition> defs = data.channelDefinitions();
    for (ChannelDefinition def : defs) {
      if (def.lead() == lead) {
        return def.index();
      }
    }
    return 0;
  }

  double[] millivolts(int channel) {
    int n = data.sampleCount();
    double[] mv = new double[n];
    for (int i = 0; i < n; i++) {
      mv[i] = data.millivolt(channel, i);
    }
    return mv;
  }
}
