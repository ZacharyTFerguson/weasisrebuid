/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import org.weasis.core.api.media.data.Thumbnail;

/** In-memory thumbnail images keyed by media URI (LRU). */
public class JIThumbnailCache {

  public static final int DEFAULT_CAPACITY = 128;

  private final int capacity;
  private final int thumbnailSize;
  private final LinkedHashMap<URI, BufferedImage> images;

  public JIThumbnailCache() {
    this(DEFAULT_CAPACITY, Thumbnail.DEFAULT_SIZE);
  }

  public JIThumbnailCache(int capacity) {
    this(capacity, Thumbnail.DEFAULT_SIZE);
  }

  public JIThumbnailCache(int capacity, int thumbnailSize) {
    this.capacity = Math.max(1, capacity);
    this.thumbnailSize = Math.min(Thumbnail.MAX_SIZE, Math.max(Thumbnail.MIN_SIZE, thumbnailSize));
    this.images =
        new LinkedHashMap<>(16, 0.75f, true) {
          @Override
          protected boolean removeEldestEntry(Map.Entry<URI, BufferedImage> eldest) {
            return size() > JIThumbnailCache.this.capacity;
          }
        };
  }

  public int capacity() {
    return capacity;
  }

  public int thumbnailSize() {
    return thumbnailSize;
  }

  public synchronized int size() {
    return images.size();
  }

  public synchronized boolean contains(URI uri) {
    return uri != null && images.containsKey(uri);
  }

  public synchronized BufferedImage get(URI uri) {
    if (uri == null) {
      return null;
    }
    return images.get(uri);
  }

  public synchronized void put(URI uri, BufferedImage image) {
    if (uri == null || image == null) {
      return;
    }
    images.put(uri, image);
  }

  public synchronized void remove(URI uri) {
    if (uri != null) {
      images.remove(uri);
    }
  }

  public synchronized void clear() {
    images.clear();
  }

  public synchronized Map<URI, BufferedImage> snapshot() {
    return Collections.unmodifiableMap(new LinkedHashMap<>(images));
  }

  public BufferedImage getOrLoad(Path path) {
    if (path == null) {
      return null;
    }
    return getOrLoad(path.toUri(), path);
  }

  public BufferedImage getOrLoad(URI uri) {
    if (uri == null) {
      return null;
    }
    Path path = null;
    if ("file".equals(uri.getScheme())) {
      path = Path.of(uri);
    }
    return getOrLoad(uri, path);
  }

  private BufferedImage getOrLoad(URI uri, Path path) {
    BufferedImage cached = get(uri);
    if (cached != null) {
      return cached;
    }
    BufferedImage loaded = read(path);
    if (loaded != null) {
      put(uri, loaded);
    }
    return get(uri);
  }

  private BufferedImage read(Path path) {
    if (path == null || !Files.isRegularFile(path)) {
      return null;
    }
    try {
      BufferedImage src = ImageIO.read(path.toFile());
      if (src == null) {
        return placeholder(path);
      }
      return scale(src);
    } catch (IOException e) {
      return placeholder(path);
    }
  }

  private BufferedImage placeholder(Path path) {
    BufferedImage image =
        new BufferedImage(thumbnailSize, thumbnailSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    int hue = Math.floorMod(Objects.hashCode(path), 360);
    g.setColor(java.awt.Color.getHSBColor(hue / 360f, 0.25f, 0.85f));
    g.fillRect(0, 0, thumbnailSize, thumbnailSize);
    g.dispose();
    return image;
  }

  private BufferedImage scale(BufferedImage src) {
    if (src.getWidth() == thumbnailSize && src.getHeight() == thumbnailSize) {
      return src;
    }
    BufferedImage dst =
        new BufferedImage(thumbnailSize, thumbnailSize, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = dst.createGraphics();
    g.setRenderingHint(
        RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(src, 0, 0, thumbnailSize, thumbnailSize, null);
    g.dispose();
    return dst;
  }
}
