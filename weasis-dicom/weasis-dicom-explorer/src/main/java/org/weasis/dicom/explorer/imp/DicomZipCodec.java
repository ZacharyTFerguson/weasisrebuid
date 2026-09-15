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

import java.net.URI;
import java.util.Hashtable;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaReader;

/** DICOM ZIP codec at the Weasis explorer path. Password ZIP uses zip4j {@code char[]}. */
@Component(service = Codec.class, immediate = true)
public class DicomZipCodec implements Codec {

  public static final String MIME = "application/dicom+zip";

  @Override
  public String getCodecName() {
    return "DicomZipCodec";
  }

  @Override
  public String[] getReaderMIMETypes() {
    return new String[] {MIME, "application/zip"};
  }

  @Override
  public String[] getReaderExtensions() {
    return new String[] {"zip"};
  }

  @Override
  public String[] getWriterMIMETypes() {
    return new String[] {MIME};
  }

  @Override
  public String[] getWriterExtensions() {
    return new String[] {"zip"};
  }

  @Override
  public boolean isMimeTypeSupported(String mimeType) {
    return MIME.equals(mimeType) || "application/zip".equals(mimeType);
  }

  @Override
  public MediaReader getMediaIO(URI media, String mimeType, Hashtable<String, Object> properties) {
    if (media == null) {
      return null;
    }
    Object pw = properties == null ? null : properties.get("zip.password");
    String password = pw instanceof String s ? s : null;
    return new DicomZipMediaIO(media, password);
  }
}
