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

import java.io.File;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.TagW;

public class DicomEncapDocElement extends MediaElement implements DicomElement, FileExtractor {
  private final DcmMediaReader mediaIO;
  private File extractFile;

  public DicomEncapDocElement(DcmMediaReader mediaIO) {
    this.mediaIO = mediaIO;
    if (mediaIO != null) {
      setMediaURI(mediaIO.getUri());
      setMimeType(DicomMime.ENCAP_DICOM);
      var dcm = mediaIO.getDicomObject();
      if (dcm != null) {
        setTag(TagW.SOPInstanceUID, dcm.getString(org.dcm4che3.data.Tag.SOPInstanceUID, ""));
      }
    }
  }

  @Override
  public DcmMediaReader getMediaReader() {
    return mediaIO;
  }

  @Override
  public String getKey() {
    Object v = getTagValue(TagW.SOPInstanceUID);
    return v == null ? "" : v.toString();
  }

  public void setExtractFile(File extractFile) {
    this.extractFile = extractFile;
  }

  @Override
  public File getExtractFile() {
    return extractFile;
  }
}
