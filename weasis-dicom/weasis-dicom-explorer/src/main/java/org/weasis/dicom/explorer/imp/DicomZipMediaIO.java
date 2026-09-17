/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.imp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.Series;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;
import org.weasis.dicom.explorer.LoadLocalDicom;
import org.weasis.dicom.explorer.SkipUnsupportedSopNotifier;

/** Media IO for a DICOM ZIP (including password ZIP). */
public class DicomZipMediaIO implements MediaReader {

  private final URI uri;
  private final String password;
  private Series<MediaElement> series;

  public DicomZipMediaIO(URI uri, String password) {
    this.uri = uri;
    this.password = password;
  }

  public LoadLocalDicom.ImportResult importZip(DicomModel model, SkipUnsupportedSopNotifier skip)
      throws IOException {
    if (uri == null) {
      throw new IOException("zip uri");
    }
    File file = new File(uri);
    return LoadLocalDicom.importZip(file, password, model, skip);
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public MediaElement getPreview() {
    MediaSeries<?> s = getMediaSeries();
    if (s == null || s.size() == 0) {
      return null;
    }
    return s.getMedias().getFirst();
  }

  @Override
  public MediaSeries<?> getMediaSeries() {
    if (series == null) {
      series = new Series<>();
      series.setMimeType(DicomZipCodec.MIME);
      try {
        LoadLocalDicom.ImportResult result = importZip(null, new SkipUnsupportedSopNotifier());
        for (ImportedInstance inst : result.imported()) {
          if (inst.file() == null) {
            continue;
          }
          MediaElement el = new MediaElement();
          el.setMediaURI(inst.file().toURI());
          el.setMimeType(inst.mime());
          series.addMedia(el);
        }
      } catch (IOException e) {
        series.setMimeType("application/zip");
      }
    }
    return series;
  }

  @Override
  public Codec getCodec() {
    return new DicomZipCodec();
  }
}
