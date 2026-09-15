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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import javax.imageio.ImageIO;
import org.osgi.service.component.annotations.Component;
import org.weasis.core.api.media.MimeInspector;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.ImageElement;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaReader;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.api.media.data.Series;

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
    if (media == null) {
      return null;
    }
    String mime = mimeType == null || mimeType.isBlank() ? guessMime(media) : mimeType;
    if (mime != null
        && !isMimeTypeSupported(mime)
        && !"application/octet-stream".equalsIgnoreCase(mime)) {
      return null;
    }
    return new ImageioMediaReader(this, media, mime == null ? "application/octet-stream" : mime);
  }

  public static String guessMime(URI media) {
    String path = media.getPath();
    if (path == null || path.isBlank()) {
      path = media.toString();
    }
    return MimeInspector.getMimeType(new File(path));
  }

  public static File localFile(URI uri) {
    if (uri == null) {
      return null;
    }
    String scheme = uri.getScheme();
    if (scheme != null && !"file".equalsIgnoreCase(scheme)) {
      return null;
    }
    try {
      if (scheme == null) {
        return new File(uri.getPath() == null ? uri.toString() : uri.getPath());
      }
      return new File(uri);
    } catch (IllegalArgumentException e) {
      String path = uri.getPath();
      return path == null ? null : new File(path);
    }
  }

  /** Stills reader. Local {@code file:} paths use ImageIO; http(s) URIs are not fetched. */
  static final class ImageioMediaReader implements MediaReader {
    private final ImageioCodec codec;
    private final URI uri;
    private final String mimeType;
    private ImageElement preview;
    private Series<MediaElement> series;

    ImageioMediaReader(ImageioCodec codec, URI uri, String mimeType) {
      this.codec = codec;
      this.uri = uri;
      this.mimeType = mimeType;
    }

    @Override
    public URI getUri() {
      return uri;
    }

    @Override
    public MediaElement getPreview() {
      ensureLoaded();
      return preview;
    }

    @Override
    public MediaSeries<?> getMediaSeries() {
      ensureLoaded();
      return series;
    }

    @Override
    public Codec getCodec() {
      return codec;
    }

    private void ensureLoaded() {
      if (preview != null) {
        return;
      }
      preview = new ImageElement(uri);
      preview.setMimeType(mimeType);
      File file = localFile(uri);
      if (file != null && file.isFile()) {
        try {
          BufferedImage image = ImageIO.read(file);
          preview.setImage(image);
        } catch (IOException ignored) {
          // pixels stay unset
        }
      }
      series = new Series<>(uri.toString());
      series.setMimeType(mimeType);
      series.addMedia(preview);
    }
  }
}
