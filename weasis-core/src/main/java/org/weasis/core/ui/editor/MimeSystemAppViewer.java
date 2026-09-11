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
import java.io.IOException;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/** Hands encapsulated PDF/video to the OS handler. */
public class MimeSystemAppViewer extends ViewerPlugin<MediaElement> {

  public MimeSystemAppViewer() {
    super("System application");
  }

  public static void openInDefaultApplication(File file) throws IOException {
    if (file == null || !file.isFile()) {
      throw new IOException("missing file");
    }
    if (!Desktop.isDesktopSupported()) {
      throw new IOException("Desktop API not supported");
    }
    Desktop.getDesktop().open(file);
  }
}
