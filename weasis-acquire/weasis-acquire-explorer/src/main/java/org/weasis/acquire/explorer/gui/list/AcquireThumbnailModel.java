/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.list;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Browse-side album of stills waiting to be imported into a dicomizer series. */
public class AcquireThumbnailModel {

  private final List<Path> items = new ArrayList<>();

  public void setItems(List<Path> items) {
    this.items.clear();
    if (items != null) {
      this.items.addAll(items);
    }
  }

  public void add(Path path) {
    if (path != null) {
      items.add(path);
    }
  }

  public void clear() {
    items.clear();
  }

  public int size() {
    return items.size();
  }

  public Path get(int index) {
    return items.get(index);
  }

  public List<Path> items() {
    return Collections.unmodifiableList(items);
  }
}
