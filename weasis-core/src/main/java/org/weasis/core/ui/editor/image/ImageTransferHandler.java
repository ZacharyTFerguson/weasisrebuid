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
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/** Clipboard / DnD of a view's raster ({@link DataFlavor#imageFlavor}). */
public class ImageTransferHandler extends TransferHandler {

  @Override
  public boolean canImport(JComponent comp, DataFlavor[] flavors) {
    return flavorIn(flavors, DataFlavor.imageFlavor);
  }

  public Transferable createTransferable(DefaultView2d<?> view) {
    return new ImageSelection(new ExportImage().render(view));
  }

  static boolean flavorIn(DataFlavor[] flavors, DataFlavor want) {
    if (flavors == null) {
      return false;
    }
    for (DataFlavor flavor : flavors) {
      if (want.equals(flavor)) {
        return true;
      }
    }
    return false;
  }

  static final class ImageSelection implements Transferable {
    private final BufferedImage image;

    ImageSelection(BufferedImage image) {
      this.image = image;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {DataFlavor.imageFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      return DataFlavor.imageFlavor.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (!isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return image;
    }
  }
}
