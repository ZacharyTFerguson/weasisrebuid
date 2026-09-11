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

import java.net.URI;

/** One frame / instance. */
public class MediaElement extends SimpleTaggable {

  private URI uri;
  private String mimeType = "application/octet-stream";

  public URI getMediaURI() {
    return uri;
  }

  public void setMediaURI(URI uri) {
    this.uri = uri;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType == null ? "application/octet-stream" : mimeType;
  }
}
