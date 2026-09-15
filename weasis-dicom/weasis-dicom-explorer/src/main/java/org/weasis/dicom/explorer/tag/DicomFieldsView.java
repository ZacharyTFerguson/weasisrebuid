/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.tag;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Sequence;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.util.TagUtils;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewCanvas;
import org.weasis.dicom.codec.DicomElement;
import org.weasis.dicom.codec.TagD;
import org.weasis.dicom.explorer.tag.AbstractTagSearchPanel.TagRow;

/**
 * Explorer DICOM Fields pane: limited clinical subset vs full dataset, with table and document
 * search. Limited hides bulk pixel/overlay/waveform payloads.
 */
public class DicomFieldsView extends JPanel {

  public static final String NAME = "DICOM Fields";
  public static final int MAX_SEQUENCE_DEPTH = 8;

  static final int[] LIMITED_TAGS = {
    Tag.SpecificCharacterSet,
    Tag.SOPClassUID,
    Tag.SOPInstanceUID,
    Tag.StudyDate,
    Tag.StudyTime,
    Tag.AccessionNumber,
    Tag.Modality,
    Tag.Manufacturer,
    Tag.InstitutionName,
    Tag.ReferringPhysicianName,
    Tag.StudyDescription,
    Tag.SeriesDescription,
    Tag.PatientName,
    Tag.PatientID,
    Tag.PatientBirthDate,
    Tag.PatientSex,
    Tag.BodyPartExamined,
    Tag.SliceThickness,
    Tag.StudyInstanceUID,
    Tag.SeriesInstanceUID,
    Tag.SeriesNumber,
    Tag.InstanceNumber,
    Tag.ImagePositionPatient,
    Tag.ImageOrientationPatient,
    Tag.FrameOfReferenceUID,
    Tag.PhotometricInterpretation,
    Tag.Rows,
    Tag.Columns,
    Tag.PixelSpacing,
    Tag.BitsAllocated,
    Tag.WindowCenter,
    Tag.WindowWidth
  };

  private final JTextField searchField = new JTextField();
  private final JCheckBox limitedBox = new JCheckBox("Limited", true);
  private final TagSearchTablePanel tablePanel = new TagSearchTablePanel(false);
  private final TagSearchDocumentPanel documentPanel = new TagSearchDocumentPanel(false);
  private Attributes dataset;

  public DicomFieldsView() {
    super(new BorderLayout());
    setName(NAME);
    searchField.setName("tagSearch");
    limitedBox.setName("limited");
    searchField.getDocument().addDocumentListener(new SimpleDocumentSync(this::syncQuery));
    limitedBox.addActionListener(e -> reload());
    JPanel north = new JPanel(new BorderLayout());
    north.add(searchField, BorderLayout.CENTER);
    north.add(limitedBox, BorderLayout.EAST);
    JTabbedPane tabs = new JTabbedPane();
    tabs.setName("tagViews");
    tabs.addTab("Table", tablePanel);
    tabs.addTab("Document", documentPanel);
    add(north, BorderLayout.NORTH);
    add(tabs, BorderLayout.CENTER);
  }

  public JTextField searchField() {
    return searchField;
  }

  public JCheckBox limitedBox() {
    return limitedBox;
  }

  public boolean isLimited() {
    return limitedBox.isSelected();
  }

  public void setLimited(boolean limited) {
    if (limitedBox.isSelected() != limited) {
      limitedBox.setSelected(limited);
    }
    reload();
  }

  public String query() {
    return searchField.getText() == null ? "" : searchField.getText();
  }

  public void setQuery(String query) {
    String next = query == null ? "" : query;
    if (!next.equals(searchField.getText())) {
      searchField.setText(next);
    }
    tablePanel.setQuery(next);
    documentPanel.setQuery(next);
  }

  public TagSearchTablePanel tablePanel() {
    return tablePanel;
  }

  public TagSearchDocumentPanel documentPanel() {
    return documentPanel;
  }

  public Attributes dataset() {
    return dataset;
  }

  public void changeDicomInfo(ViewCanvas view, MediaElement img) {
    changeDicomInfo(img);
  }

  public void changeDicomInfo(MediaElement img) {
    if (img instanceof DicomElement element) {
      changeDicomInfo(element.getDicomObject());
      return;
    }
    changeDicomInfo((Attributes) null);
  }

  public void changeDicomInfo(DicomElement element) {
    changeDicomInfo(element == null ? null : element.getDicomObject());
  }

  public void changeDicomInfo(Attributes dataset) {
    this.dataset = dataset;
    reload();
  }

  public List<TagRow> allItems() {
    return tablePanel.items();
  }

  public List<TagRow> visibleItems() {
    return tablePanel.filtered();
  }

  void reload() {
    List<TagRow> rows = collect(dataset, isLimited());
    tablePanel.setItems(rows);
    documentPanel.setItems(rows);
    String q = query();
    tablePanel.setQuery(q);
    documentPanel.setQuery(q);
  }

  void syncQuery() {
    String q = query();
    tablePanel.setQuery(q);
    documentPanel.setQuery(q);
  }

  public static List<TagRow> collect(Attributes dataset, boolean limited) {
    List<TagRow> rows = new ArrayList<>();
    if (dataset == null) {
      return rows;
    }
    if (limited) {
      for (int tag : LIMITED_TAGS) {
        if (dataset.contains(tag)) {
          append(dataset, tag, 0, rows, true);
        }
      }
      return rows;
    }
    for (int tag : dataset.tags()) {
      append(dataset, tag, 0, rows, false);
    }
    return rows;
  }

  static void append(Attributes dataset, int tag, int depth, List<TagRow> rows, boolean limited) {
    if (dataset == null || depth > MAX_SEQUENCE_DEPTH) {
      return;
    }
    VR vr = dataset.getVR(tag);
    String keyword = TagD.keywordOf(tag);
    String hex = TagUtils.toString(tag);
    String vrName = vr == null ? "" : vr.name();
    if (vr == VR.SQ) {
      Sequence seq = dataset.getSequence(tag);
      int n = seq == null ? 0 : seq.size();
      rows.add(new TagRow(tag, keyword, hex, vrName, "SQ[" + n + "]", depth));
      if (!limited && seq != null) {
        int item = 1;
        for (Attributes child : seq) {
          rows.add(new TagRow(tag, "Item", hex, "SQ", String.valueOf(item), depth + 1));
          if (child != null) {
            for (int nested : child.tags()) {
              append(child, nested, depth + 2, rows, false);
            }
          }
          item++;
        }
      }
      return;
    }
    rows.add(new TagRow(tag, keyword, hex, vrName, formatValue(dataset, tag, vr), depth));
  }

  static String formatValue(Attributes dataset, int tag, VR vr) {
    if (isBulkBinary(tag, vr)) {
      return "[" + (vr == null ? "UN" : vr.name()) + "]";
    }
    try {
      String[] values = dataset.getStrings(tag);
      if (values == null || values.length == 0) {
        return "";
      }
      return String.join("\\", values);
    } catch (RuntimeException e) {
      return "[" + (vr == null ? "UN" : vr.name()) + "]";
    }
  }

  public static boolean isBulkBinary(int tag, VR vr) {
    if (tag == Tag.PixelData || tag == Tag.WaveformData || tag == Tag.OverlayData) {
      return true;
    }
    int group = TagUtils.groupNumber(tag);
    int element = TagUtils.elementNumber(tag);
    if (group >= 0x6000 && group <= 0x60FF && (group & 1) == 0 && element == 0x3000) {
      return true;
    }
    return vr == VR.OB
        || vr == VR.OW
        || vr == VR.OF
        || vr == VR.OD
        || vr == VR.OL
        || vr == VR.OV
        || vr == VR.UN;
  }

  /** DocumentListener adapter that avoids an extra named type in main/java. */
  private static final class SimpleDocumentSync implements javax.swing.event.DocumentListener {
    private final Runnable onChange;

    SimpleDocumentSync(Runnable onChange) {
      this.onChange = onChange;
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) {
      onChange.run();
    }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) {
      onChange.run();
    }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) {
      onChange.run();
    }
  }
}
