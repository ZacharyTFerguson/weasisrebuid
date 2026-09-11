/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.model;

/** Shared SDK timing counters used by explorers and viewers. */
public class PerformanceModel {

  private long decodeNanos;
  private long displayNanos;
  private int imageCount;

  public long getDecodeNanos() {
    return decodeNanos;
  }

  public void setDecodeNanos(long decodeNanos) {
    this.decodeNanos = decodeNanos;
  }

  public long getDisplayNanos() {
    return displayNanos;
  }

  public void setDisplayNanos(long displayNanos) {
    this.displayNanos = displayNanos;
  }

  public int getImageCount() {
    return imageCount;
  }

  public void setImageCount(int imageCount) {
    this.imageCount = imageCount;
  }
}
