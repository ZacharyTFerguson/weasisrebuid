/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.media;

import java.util.Objects;

/** Named stills source shown in the dicomizer browse combo. */
public class MediaSource {

  private String id;
  private String displayName;

  public MediaSource(String id, String displayName) {
    this.id = id == null ? "" : id;
    this.displayName = displayName == null || displayName.isBlank() ? this.id : displayName;
  }

  public String getID() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  protected void setID(String id) {
    this.id = id == null ? "" : id;
  }

  protected void setDisplayName(String displayName) {
    this.displayName = displayName == null || displayName.isBlank() ? this.id : displayName;
  }

  @Override
  public String toString() {
    return displayName;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof MediaSource other)) {
      return false;
    }
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
