/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.launcher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileUtilTest {

  @TempDir Path tempDir;

  @Test
  void deletesDirectoryTree() throws Exception {
    Path nested = tempDir.resolve("a/b/c.txt");
    Files.createDirectories(nested.getParent());
    Files.writeString(nested, "x");
    assertTrue(FileUtil.delete(tempDir.resolve("a").toFile()));
    assertFalse(Files.exists(tempDir.resolve("a")));
  }

  @Test
  void stripsExtensionAndIgnoresDotfiles() {
    assertEquals("study", FileUtil.nameWithoutExtension("study.dcm"));
    assertEquals("archive.tar", FileUtil.nameWithoutExtension("/tmp/archive.tar.gz"));
    assertEquals(".gitignore", FileUtil.nameWithoutExtension(".gitignore"));
    assertEquals("README", FileUtil.nameWithoutExtension("README"));
  }

  @Test
  void matchesExtensionsCaseInsensitively() {
    assertTrue(FileUtil.hasExtension("image.DCM", "dcm"));
    assertTrue(FileUtil.hasExtension("image.dcm", ".DCM"));
    assertFalse(FileUtil.hasExtension("image.dcm", "zip"));
  }

  @Test
  void writesStreamAndReportsSize() throws Exception {
    Path out = tempDir.resolve("out/bytes.txt");
    FileUtil.write(new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8)), out);
    assertEquals("hello", Files.readString(out));
    assertEquals("5 B", FileUtil.humanReadableByteCount(5));
    assertEquals("1.0 KB", FileUtil.humanReadableByteCount(1024));
  }
}
