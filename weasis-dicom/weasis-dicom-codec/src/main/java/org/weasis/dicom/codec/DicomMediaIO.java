/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.codec;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.io.DicomInputStream;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.Series;
import org.weasis.core.api.media.data.TagW;

/** Part-10 reader via weasis-dicom-tools / dcm4che. */
public class DicomMediaIO implements MediaReader {

  private final Attributes dataset;
  private final String transferSyntax;
  private final URI uri;
  private final Codec codec;

  public DicomMediaIO(Attributes dataset, String transferSyntax) {
    this(dataset, transferSyntax, null, null);
  }

  public DicomMediaIO(Attributes dataset, String transferSyntax, URI uri, Codec codec) {
    this.dataset = dataset;
    this.transferSyntax = transferSyntax;
    this.uri = uri;
    this.codec = codec;
  }

  public static DicomMediaIO open(File file) throws IOException {
    return open(file, null);
  }

  public static DicomMediaIO open(File file, Codec codec) throws IOException {
    try (DicomInputStream in = new DicomInputStream(file)) {
      in.setIncludeBulkData(DicomInputStream.IncludeBulkData.YES);
      Attributes dataset = in.readDataset();
      String ts = in.getTransferSyntax();
      Attributes fmi = in.getFileMetaInformation();
      if (ts == null && fmi != null) {
        ts = fmi.getString(Tag.TransferSyntaxUID, UID.ExplicitVRLittleEndian);
      }
      if (ts == null) {
        ts = UID.ExplicitVRLittleEndian;
      }
      return new DicomMediaIO(dataset, ts, file.toURI(), codec);
    }
  }

  public Attributes getDataset() {
    return dataset;
  }

  public String getTransferSyntax() {
    return transferSyntax;
  }

  public String mimeType() {
    TransferSyntax ts = TransferSyntax.forUid(transferSyntax).orElse(null);
    if (ts != null && ts.isVideo()) {
      return DicomMime.VIDEO_DICOM;
    }
    return DicomMime.fromSopClass(dataset.getString(Tag.SOPClassUID, ""));
  }

  /**
   * @see DicomUnderstandingLimits#canPaintWindowLevel(String, Attributes)
   */
  public boolean isExplicitVrLeMonochrome2() {
    return DicomUnderstandingLimits.canPaintWindowLevel(transferSyntax, dataset);
  }

  /** Same paint path as View2d W/L; throws when {@link #isExplicitVrLeMonochrome2()} is false. */
  public BufferedImage paintWindowLevel() {
    if (!isExplicitVrLeMonochrome2()) {
      throw new IllegalStateException("outside DicomUnderstandingLimits");
    }
    return WindowLevelPainter.paintMonochrome2(dataset);
  }

  @Override
  public URI getUri() {
    return uri;
  }

  @Override
  public MediaElement getPreview() {
    MediaElement element = new MediaElement();
    element.setMediaURI(uri);
    element.setMimeType(mimeType());
    element.setTag(TagW.SOPInstanceUID, dataset.getString(Tag.SOPInstanceUID, ""));
    element.setTag(TagW.Modality, dataset.getString(Tag.Modality, ""));
    return element;
  }

  @Override
  public MediaSeries<?> getMediaSeries() {
    Series<MediaElement> series = new Series<>(dataset.getString(Tag.SeriesInstanceUID, "series"));
    series.setMimeType(DicomMime.SERIES_DICOM);
    series.addMedia(getPreview());
    return series;
  }

  @Override
  public Codec getCodec() {
    return codec;
  }
}
