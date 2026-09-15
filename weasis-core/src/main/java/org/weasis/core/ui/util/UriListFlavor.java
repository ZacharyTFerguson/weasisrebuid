/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.util;

import java.awt.datatransfer.DataFlavor;

public final class UriListFlavor {
  public static final DataFlavor flavor;

  static {
    DataFlavor f;
    try {
      f = new DataFlavor("text/uri-list;class=java.lang.String");
    } catch (ClassNotFoundException e) {
      f = DataFlavor.stringFlavor;
    }
    flavor = f;
  }

  private UriListFlavor() {}
}
