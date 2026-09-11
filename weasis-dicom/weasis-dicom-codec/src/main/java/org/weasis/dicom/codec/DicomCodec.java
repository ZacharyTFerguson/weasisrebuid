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

import java.net.URI;
import java.nio.file.Path;
import java.util.Hashtable;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaReader;

@Component(service = Codec.class, immediate = true)
public class DicomCodec implements Codec {

  @Override
  public String getCodecName() {
    return "DicomCodec";
  }

  @Override
  public String[] getReaderMIMETypes() {
    return new String[] {
      DicomMime.APPLICATION_DICOM, DicomMime.IMAGE_DICOM, DicomMime.SERIES_DICOM
    };
  }

  @Override
  public String[] getReaderExtensions() {
    return new String[] {"dcm", "dicom"};
  }

  @Override
  public String[] getWriterMIMETypes() {
    return new String[] {DicomMime.APPLICATION_DICOM};
  }

  @Override
  public String[] getWriterExtensions() {
    return new String[] {"dcm"};
  }

  @Override
  public boolean isMimeTypeSupported(String mimeType) {
    if (mimeType == null) {
      return false;
    }
    return mimeType.startsWith("application/dicom")
        || mimeType.startsWith("image/dicom")
        || mimeType.startsWith("series/dicom")
        || mimeType.equals(DicomMime.PR_DICOM)
        || mimeType.equals(DicomMime.KO_DICOM)
        || mimeType.equals(DicomMime.SEG_DICOM);
  }

  @Override
  public MediaReader getMediaIO(URI media, String mimeType, Hashtable<String, Object> properties) {
    if (media == null || media.getScheme() == null || !"file".equalsIgnoreCase(media.getScheme())) {
      return null;
    }
    try {
      return DicomMediaIO.open(Path.of(media).toFile(), this);
    } catch (Exception e) {
      return null;
    }
  }
}
