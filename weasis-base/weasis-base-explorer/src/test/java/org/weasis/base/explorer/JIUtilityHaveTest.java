/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JIUtilityHaveTest {

  @Test
  void fileNameExtensionAndImageVsText() {
    Path png = Path.of("cell.png");
    Path txt = Path.of("notes.txt");
    assertEquals("cell.png", JIUtility.fileName(png));
    assertEquals("png", JIUtility.extension(png));
    assertTrue(JIUtility.isImageFile(png));
    assertFalse(JIUtility.isImageFile(txt));
    assertEquals("", JIUtility.fileName(null));
    assertEquals("", JIUtility.extension(Path.of("noext")));
  }

  @Test
  void formatSizeAndNewContext() {
    assertEquals("500 B", JIUtility.formatSize(500));
    assertEquals("1.0 KB", JIUtility.formatSize(1024));
    assertEquals("1.0 MB", JIUtility.formatSize(1024L * 1024L));
    assertInstanceOf(JIExplorerContext.class, JIUtility.newContext());
    assertInstanceOf(JIExplorerContext.class, new DefaultExplorer().explorerContext());
  }

  @Test
  void rendererTextUsesFileName(@TempDir Path dir) throws Exception {
    Path png = dir.resolve("cell.png");
    Files.write(png, new byte[] {1, 2, 3});
    ThumbnailRenderer renderer = new ThumbnailRenderer();
    renderer.getListCellRendererComponent(new JList<>(), png, 0, false, false);
    assertEquals("cell.png", renderer.getText());
    renderer.getListCellRendererComponent(new JList<>(), null, 0, false, false);
    assertEquals("", renderer.getText());
  }
}
