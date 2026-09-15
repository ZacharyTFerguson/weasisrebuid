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

import java.io.File;
import java.net.URI;

public class FileCache {
  private final MediaElement element;
  private File original;
  private File transformed;

  public FileCache(MediaElement element) {
    this.element = element;
    if (element != null && element.getMediaURI() != null) {
      URI uri = element.getMediaURI();
      if ("file".equalsIgnoreCase(uri.getScheme())) {
        this.original = new File(uri);
      }
    }
  }

  public MediaElement getElement() {
    return element;
  }

  public File getOriginalFile() {
    return original;
  }

  public void setOriginalTempFile(File original) {
    this.original = original;
  }

  public File getTransformedFile() {
    return transformed == null ? original : transformed;
  }

  public void setTransformedFile(File transformed) {
    this.transformed = transformed;
  }

  public boolean isElementInMemory() {
    return original == null;
  }
}
