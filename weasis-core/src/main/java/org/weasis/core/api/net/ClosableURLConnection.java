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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class ClosableURLConnection implements Closeable {
  private final InputStream stream;

  public ClosableURLConnection(URI uri, URLParameters params) throws Exception {
    this.stream = HttpUtils.get(uri, params);
  }

  public InputStream getInputStream() {
    return stream;
  }

  @Override
  public void close() throws IOException {
    if (stream != null) {
      stream.close();
    }
  }
}
