/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.imageio.codec;

import java.net.URI;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaReader;

/** ImageIO stills (non-DICOM). */
@Component(service = Codec.class, immediate = true)
public class ImageioCodec implements Codec {

  private static final Set<String> MIME =
      Set.of("image/jpeg", "image/png", "image/bmp", "image/gif", "image/tiff");

  @Override
  public String getCodecName() {
    return "ImageioCodec";
  }

  @Override
  public String[] getReaderMIMETypes() {
    return MIME.toArray(String[]::new);
  }

  @Override
  public String[] getReaderExtensions() {
    return new String[] {"jpg", "jpeg", "png", "bmp", "gif", "tif", "tiff"};
  }

  @Override
  public String[] getWriterMIMETypes() {
    return ImageIO.getWriterMIMETypes();
  }

  @Override
  public String[] getWriterExtensions() {
    return ImageIO.getWriterFileSuffixes();
  }

  @Override
  public boolean isMimeTypeSupported(String mimeType) {
    return mimeType != null && MIME.contains(mimeType.toLowerCase(Locale.ROOT));
  }

  @Override
  public MediaReader getMediaIO(URI media, String mimeType, Hashtable<String, Object> properties) {
    return null;
  }
}
