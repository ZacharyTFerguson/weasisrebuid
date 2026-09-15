/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.net;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class URLParameters {
  private final Map<String, String> headers;
  private final boolean httpPost;

  public URLParameters() {
    this(Map.of(), false);
  }

  public URLParameters(Map<String, String> headers, boolean httpPost) {
    this.headers =
        headers == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(headers));
    this.httpPost = httpPost;
  }

  public Map<String, String> getUnmodifiableHeaders() {
    return headers;
  }

  public boolean isHttpPost() {
    return httpPost;
  }
}
