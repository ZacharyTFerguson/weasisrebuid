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

public class RequestStallGuard {
  private final StallWatchdog watchdog;

  public RequestStallGuard(long timeoutMillis) {
    this.watchdog = new StallWatchdog(timeoutMillis);
  }

  public StallWatchdog getWatchdog() {
    return watchdog;
  }

  public void check() throws StallTimeoutException {
    if (watchdog.stalled()) {
      throw new StallTimeoutException("HTTP stall");
    }
  }
}
