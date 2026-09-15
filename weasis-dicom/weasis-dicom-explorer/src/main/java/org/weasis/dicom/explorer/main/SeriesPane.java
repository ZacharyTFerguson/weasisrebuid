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
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.SeriesThumbnail;
import org.weasis.core.api.media.data.Thumbnail;
import org.weasis.core.ui.editor.image.ViewTransferHandler;
import org.weasis.dicom.codec.DicomMime;
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
      addThumb(inst, index);
      index++;
    }
    revalidate();
    repaint();
  }

  void addThumb(ImportedInstance inst, int idx) {
    SeriesThumbnail thumb = thumbnailFor(inst);
    bindThumb(thumb, idx);
    thumbs.add(thumb);
    thumbnails.add(thumb);
  }

  SeriesThumbnail thumbnailFor(ImportedInstance inst) {
    SeriesThumbnail thumb = new SeriesThumbnail(seriesFor(inst), Thumbnail.MIN_SIZE);
    String label = inst.modality() + " #" + inst.seriesNumber() + " " + inst.seriesDescription();
    thumb.setToolTipText(label);
    thumb.setName(inst.seriesUid());
    thumb.setOverlayIcon(DicomMime.overlayIcon(inst.mime()));
    thumb.setTransferHandler(new ViewTransferHandler());
    return thumb;
  }

  Series<MediaElement> seriesFor(ImportedInstance inst) {
    Series<MediaElement> series = new Series<>(inst.seriesUid());
    series.setMimeType(inst.mime());
    series.addMedia(mediaOf(inst));
    return series;
  }

  static MediaElement mediaOf(ImportedInstance inst) {
    MediaElement el = new MediaElement();
    el.setMimeType(inst.mime());
    if (inst.file() != null) {
      el.setMediaURI(inst.file().toURI());
    }
    return el;
  }

  void bindThumb(SeriesThumbnail thumb, int idx) {
    ThumbDrag drag = new ThumbDrag();
    thumb.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mousePressed(MouseEvent e) {
            drag.press();
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
    thumb.addMouseMotionListener(
        new MouseMotionAdapter() {
          @Override
          public void mouseDragged(MouseEvent e) {
            drag.drag(thumb, e);
          }
        });
  }

  static void exportThumb(SeriesThumbnail thumb, MouseEvent e) {
    TransferHandler handler = thumb.getTransferHandler();
    if (handler == null) {
      return;
    }
    ViewTransferHandler.beginDrag(thumb.getSeries());
    handler.exportAsDrag(thumb, e, TransferHandler.COPY);
  }

  /** One {@link TransferHandler#exportAsDrag} per press; repeating drag events abort Swing DnD. */
  static final class ThumbDrag {
    private boolean started;

    void press() {
      started = false;
    }

    void drag(SeriesThumbnail thumb, MouseEvent e) {
      if (started) {
        return;
      }
      started = true;
      exportThumb(thumb, e);
    }
  }
}
