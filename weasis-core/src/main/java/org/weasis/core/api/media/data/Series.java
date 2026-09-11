/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.media.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Series<E extends MediaElement> extends MediaSeriesGroupNode implements MediaSeries<E> {

  private final List<E> medias = new ArrayList<>();
  private String mimeType = "application/octet-stream";

  public Series() {
    this(UUID.randomUUID().toString());
  }

  public Series(String seriesUid) {
    super(TagW.SeriesInstanceUID, seriesUid);
  }

  @Override
  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType == null ? "application/octet-stream" : mimeType;
    setTag(TagW.MIME, this.mimeType);
  }

  @Override
  public synchronized void addMedia(E media) {
    if (media != null) {
      medias.add(media);
    }
  }

  @Override
  public synchronized List<E> getMedias() {
    return Collections.unmodifiableList(new ArrayList<>(medias));
  }

  @Override
  public synchronized int size() {
    return medias.size();
  }
}
