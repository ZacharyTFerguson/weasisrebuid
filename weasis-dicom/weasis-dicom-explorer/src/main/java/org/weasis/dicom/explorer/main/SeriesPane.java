/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.media.data.Thumbnail;
import org.weasis.dicom.explorer.DicomSorter;
import org.weasis.dicom.explorer.ImportedInstance;

/** Explorer series thumbnail strip. Selection follows SHORTCUTS.md Explorer. */
public class SeriesPane extends JPanel {

  private final SeriesSelectionModel selection = new SeriesSelectionModel();
  private final ThumbnailMouseAndKeyAdapter adapter = new ThumbnailMouseAndKeyAdapter(selection);
  private final JPanel thumbs = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
  private final List<SeriesThumbnail> thumbnails = new ArrayList<>();
  private Runnable onOpen = () -> {};

  public SeriesPane() {
    super(new BorderLayout());
    add(new JScrollPane(thumbs), BorderLayout.CENTER);
    thumbs.setFocusable(true);
    thumbs.addKeyListener(adapter.keyAdapter());
  }

  public SeriesSelectionModel getSelectionModel() {
    return selection;
  }

  public ThumbnailMouseAndKeyAdapter getAdapter() {
    return adapter;
  }

  public void setOnOpen(Runnable onOpen) {
    this.onOpen = onOpen == null ? () -> {} : onOpen;
  }

  public List<SeriesThumbnail> thumbnails() {
    return List.copyOf(thumbnails);
  }

  public void showThumbnails(List<ImportedInstance> instances) {
    thumbs.removeAll();
    thumbnails.clear();
    Map<String, ImportedInstance> unique = new LinkedHashMap<>();
    for (ImportedInstance inst :
        DicomSorter.sortSeries(instances == null ? List.of() : instances)) {
      unique.putIfAbsent(inst.seriesUid(), inst);
    }
    int index = 0;
    for (ImportedInstance inst : unique.values()) {
      Series<MediaElement> series = new Series<>(inst.seriesUid());
      series.setMimeType(inst.mime());
      SeriesThumbnail thumb = new SeriesThumbnail(series, Thumbnail.MIN_SIZE);
      String label = inst.modality() + " #" + inst.seriesNumber() + " " + inst.seriesDescription();
      thumb.setToolTipText(label);
      thumb.setName(inst.seriesUid());
      int idx = index;
      thumb.addMouseListener(
          new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
              adapter.pressed(idx, e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
              if (e.getClickCount() == 2) {
                adapter.getModel().enter();
                onOpen.run();
              }
            }
          });
      thumbs.add(thumb);
      thumbnails.add(thumb);
      index++;
    }
    revalidate();
    repaint();
  }
}
