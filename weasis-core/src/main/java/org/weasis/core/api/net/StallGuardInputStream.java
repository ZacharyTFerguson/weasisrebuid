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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StallGuardInputStream extends FilterInputStream {
  private final StallWatchdog watchdog;

  public StallGuardInputStream(InputStream in, StallWatchdog watchdog) {
    super(in);
    this.watchdog = watchdog;
  }

  @Override
  public int read() throws IOException {
    int v = super.read();
    if (watchdog != null) {
      watchdog.tick();
    }
    return v;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int n = super.read(b, off, len);
    if (watchdog != null) {
      watchdog.tick();
    }
    return n;
  }
}
