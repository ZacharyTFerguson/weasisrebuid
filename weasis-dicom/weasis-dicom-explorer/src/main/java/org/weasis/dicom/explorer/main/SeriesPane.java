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
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
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
    ThumbDrag drag = new ThumbDrag(thumb, idx);
    thumb.addMouseListener(drag);
    thumb.addMouseMotionListener(drag);
  }

  /**
   * Arm {@code dragging}/{@code lastDragged} on left press. Do not {@code exportAsDrag}: X11 native
   * drag swallows drop and {@code dragDropEnd}. Toolkit {@code MOUSE_RELEASED} while {@code
   * dragging()} hangs the empty View2d under the pointer.
   */
  final class ThumbDrag extends MouseAdapter {
    private final SeriesThumbnail thumb;
    private final int idx;
    private Point origin;
    private boolean started;

    ThumbDrag(SeriesThumbnail thumb, int idx) {
      this.thumb = thumb;
      this.idx = idx;
    }

    @Override
    public void mousePressed(MouseEvent e) {
      ViewTransferHandler.clearDragged();
      origin = SwingUtilities.isLeftMouseButton(e) ? e.getPoint() : null;
      started = false;
      adapter.pressed(idx, e);
      if (origin != null) {
        ViewTransferHandler.beginDrag(thumb.getSeries());
        e.consume();
      }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        adapter.getModel().enter();
        onOpen.run();
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (origin == null) {
        return;
      }
      startIfFar(e);
      track(e);
      e.consume();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      ViewTransferHandler.overAt(new Point(e.getXOnScreen(), e.getYOnScreen()));
      ViewTransferHandler.hangAtPointer();
      ViewTransferHandler.endDrag();
    }

    void startIfFar(MouseEvent e) {
      if (!started && origin.distance(e.getPoint()) >= DragSource.getDragThreshold()) {
        ViewTransferHandler.beginDrag(thumb.getSeries());
        started = true;
      }
    }

    void track(MouseEvent e) {
      if (started) {
        ViewTransferHandler.overAt(new Point(e.getXOnScreen(), e.getYOnScreen()));
      }
    }
  }
}
