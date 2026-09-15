/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.editor;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public final class DefaultMimeIO {
  private DefaultMimeIO() {}

  public static void open(URI uri) {
    if (uri == null || !Desktop.isDesktopSupported()) {
      return;
    }
    try {
      if ("file".equalsIgnoreCase(uri.getScheme())) {
        Desktop.getDesktop().open(new File(uri));
      } else {
        Desktop.getDesktop().browse(uri);
      }
    } catch (Exception ignored) {
      // system app optional
    }
  }
}
