/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.TransferHandler;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.service.UICore;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.ViewerPluginBuilder;
import org.weasis.core.ui.editor.image.ViewerPlugin;
import org.weasis.dicom.codec.DicomMime;
import org.weasis.dicom.explorer.main.SplitSeriesManager;

/**
 * Groups imported SOP instances by Series Instance UID and opens each series with the MIME factory
 * that {@link UICore#getViewerFactory(String)} selects. {@link MimeSystemAppFactory} handles PDF /
 * video; it must not win {@code image/dicom}.
 */
public class DicomSeriesHandler extends TransferHandler {

  public record SeriesBucket(String seriesUid, String mime, List<ImportedInstance> instances) {
    public SeriesBucket {
      instances = instances == null ? List.of() : List.copyOf(instances);
      mime = mime == null || mime.isBlank() ? DicomMime.IMAGE_DICOM : mime;
      seriesUid = seriesUid == null ? "" : seriesUid;
    }
  }

  private final DicomModel model;
  private final SkipUnsupportedSopNotifier skip;

  public DicomSeriesHandler() {
    this(new DicomModel(), new SkipUnsupportedSopNotifier());
  }

  public DicomSeriesHandler(DicomModel model) {
    this(model, new SkipUnsupportedSopNotifier());
  }

  public DicomSeriesHandler(DicomModel model, SkipUnsupportedSopNotifier skip) {
    this.model = model == null ? new DicomModel() : model;
    this.skip = skip == null ? new SkipUnsupportedSopNotifier() : skip;
  }

  public DicomModel getDicomModel() {
    return model;
  }

  public List<SeriesBucket> group(List<ImportedInstance> instances) {
    List<ImportedInstance> source = instances == null ? List.of() : instances;
    return bucketsOf(new SplitSeriesManager().rewrite(source));
  }

  static List<SeriesBucket> bucketsOf(List<ImportedInstance> instances) {
    Map<String, List<ImportedInstance>> map = new LinkedHashMap<>();
    for (ImportedInstance inst : instances) {
      if (inst != null) {
        map.computeIfAbsent(inst.seriesUid(), k -> new ArrayList<>()).add(inst);
      }
    }
    return toBuckets(map);
  }

  static List<SeriesBucket> toBuckets(Map<String, List<ImportedInstance>> map) {
    List<SeriesBucket> buckets = new ArrayList<>();
    for (Map.Entry<String, List<ImportedInstance>> e : map.entrySet()) {
      List<ImportedInstance> sorted = DicomSorter.sortInstances(e.getValue());
      buckets.add(new SeriesBucket(e.getKey(), mimeOf(sorted), sorted));
    }
    return buckets;
  }

  static String mimeOf(List<ImportedInstance> sorted) {
    return sorted.isEmpty() ? DicomMime.IMAGE_DICOM : sorted.getFirst().mime();
  }

  public List<SeriesBucket> group(DicomModel source) {
    return group(source == null ? List.of() : source.getInstances());
  }

  public Series<MediaElement> toMediaSeries(SeriesBucket bucket) {
    Series<MediaElement> series = new Series<>(bucket.seriesUid());
    series.setMimeType(bucket.mime());
    for (ImportedInstance inst : bucket.instances()) {
      MediaElement el = new MediaElement();
      if (inst.file() != null) {
        el.setMediaURI(inst.file().toURI());
      }
      el.setMimeType(inst.mime());
      series.addMedia(el);
    }
    return series;
  }

  public SeriesViewerFactory factoryFor(String mime, UICore core) {
    if (mime == null || core == null) {
      return null;
    }
    return core.getViewerFactory(mime).orElse(null);
  }

  public ViewerPlugin<?> open(SeriesBucket bucket, UICore core) {
    if (bucket == null || core == null) {
      return null;
    }
    SeriesViewerFactory factory = factoryFor(bucket.mime(), core);
    if (factory == null) {
      return null;
    }
    return ViewerPluginBuilder.openSequenceInPlugin(
        core, factory, toMediaSeries(bucket), new Hashtable<>(), true, true);
  }

  public LoadLocalDicom.ImportResult handleFiles(List<File> files) throws IOException {
    return new LoadDicom(model, files, null, skip).load();
  }

  @Override
  public boolean canImport(TransferSupport support) {
    return support != null && support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
  }

  @Override
  public boolean importData(TransferSupport support) {
    if (!canImport(support)) {
      return false;
    }
    try {
      Transferable transferable = support.getTransferable();
      Object data = transferable.getTransferData(DataFlavor.javaFileListFlavor);
      if (data instanceof List<?> list) {
        List<File> files = new ArrayList<>();
        for (Object o : list) {
          if (o instanceof File file) {
            files.add(file);
          }
        }
        handleFiles(files);
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }
}
