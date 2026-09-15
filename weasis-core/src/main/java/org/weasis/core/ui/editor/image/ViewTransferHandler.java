/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.weasis.core.ui.util.UriListFlavor;

/** Drop files / URI-list onto a view (series import). Not bound to the 2×2 grid. */
public class ViewTransferHandler extends TransferHandler {

  private List<File> lastFiles = List.of();

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return fileFlavor(flavors) || uriFlavor(flavors);
  }

  boolean fileFlavor(DataFlavor[] flavors) {
    return ImageTransferHandler.flavorIn(flavors, DataFlavor.javaFileListFlavor);
  }

  boolean uriFlavor(DataFlavor[] flavors) {
    return ImageTransferHandler.flavorIn(flavors, UriListFlavor.flavor);
  }

  public int importFiles(List<File> files) {
    if (files == null || files.isEmpty()) {
      return 0;
    }
    lastFiles = List.copyOf(files);
    return lastFiles.size();
  }

  public List<File> lastFiles() {
    return lastFiles;
  }
}
