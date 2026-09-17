/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.core.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireMediaInfo;
import org.weasis.acquire.explorer.gui.central.SeriesDataListener;
import org.weasis.core.api.media.data.TagW;

/** Named album series of {@link AcquireImageInfo}/{@link AcquireMediaInfo}. */
public class SeriesGroup extends DefaultTaggable {

  public enum Type {
    NONE,
    DATE,
    NAME
  }

  public static final String DEFAULT_NAME = "Series";

  private final Type type;
  private String name;
  private final List<AcquireMediaInfo> media = new ArrayList<>();
  private final List<SeriesDataListener> listeners = new ArrayList<>();

  public SeriesGroup() {
    this(Type.NONE, DEFAULT_NAME);
  }

  public SeriesGroup(Type type, String name) {
    this.type = type == null ? Type.NONE : type;
    this.name = normalize(name);
    setTag(TagW.SeriesDescription, this.name);
  }

  public Type getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = normalize(name);
    setTag(TagW.SeriesDescription, this.name);
  }

  public List<AcquireMediaInfo> getMedia() {
    return Collections.unmodifiableList(media);
  }

  public List<AcquireImageInfo> getImages() {
    List<AcquireImageInfo> images = new ArrayList<>();
    for (AcquireMediaInfo item : media) {
      if (item instanceof AcquireImageInfo image) {
        images.add(image);
      }
    }
    return List.copyOf(images);
  }

  public void add(AcquireMediaInfo info) {
    if (info == null || media.contains(info)) {
      return;
    }
    media.add(info);
    info.setSeriesGroup(this);
    fireSeriesChanged();
  }

  public boolean remove(AcquireMediaInfo info) {
    boolean removed = media.remove(info);
    if (removed) {
      if (info != null && info.getSeriesGroup() == this) {
        info.setSeriesGroup(null);
      }
      fireSeriesChanged();
    }
    return removed;
  }

  public void addSeriesListener(SeriesDataListener listener) {
    if (listener != null && !listeners.contains(listener)) {
      listeners.add(listener);
    }
  }

  public void removeSeriesListener(SeriesDataListener listener) {
    listeners.remove(listener);
  }

  private void fireSeriesChanged() {
    for (SeriesDataListener listener : List.copyOf(listeners)) {
      listener.seriesChanged(this);
    }
  }

  static String normalize(String name) {
    if (name == null || name.isBlank()) {
      return DEFAULT_NAME;
    }
    return name.trim();
  }
}
