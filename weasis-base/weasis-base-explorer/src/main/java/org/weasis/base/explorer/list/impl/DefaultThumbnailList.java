/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list.impl;

import java.awt.image.BufferedImage;
import org.weasis.base.explorer.JIThumbnailCache;
import org.weasis.base.explorer.list.AbstractThumbnailList;

/** Default non-DICOM thumbnail list backed by {@link JIThumbnailCache}. */
public class DefaultThumbnailList extends AbstractThumbnailList {

  private final JIThumbnailCache cache;

  public DefaultThumbnailList() {
    this(new JIThumbnailCache());
  }

  public DefaultThumbnailList(JIThumbnailCache cache) {
    this.cache = cache == null ? new JIThumbnailCache() : cache;
  }

  public JIThumbnailCache getCache() {
    return cache;
  }

  public BufferedImage thumbnailAt(int index) {
    var path = get(index);
    if (path == null) {
      return null;
    }
    return cache.getOrLoad(path);
  }
}
