/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.exp;

import javax.swing.SwingWorker;

/** Background import/export worker (Have: interruptible flag + message). */
public abstract class ExplorerTask<T, V> extends SwingWorker<T, V> {

  private final String message;
  private final boolean interruptible;

  protected ExplorerTask(String message, boolean interruptible) {
    this.message = message == null ? "" : message;
    this.interruptible = interruptible;
  }

  public String getMessage() {
    return message;
  }

  public boolean isInterruptible() {
    return interruptible;
  }
}
