/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.concurrent.atomic.AtomicBoolean;

/** Cooperative cancel for a retrieve without tearing down DIMSE sockets in Have tests. */
public class GracefulCancel {

  private final AtomicBoolean cancelled = new AtomicBoolean();

  public void cancel() {
    cancelled.set(true);
  }

  public boolean isCancelled() {
    return cancelled.get();
  }

  public void reset() {
    cancelled.set(false);
  }
}
