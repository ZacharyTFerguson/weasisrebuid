/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.vol;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ChunkedMappedBuffer implements AutoCloseable {
  private RandomAccessFile raf;
  private MappedByteBuffer buffer;

  public ChunkedMappedBuffer(File file) throws Exception {
    if (file == null) {
      return;
    }
    this.raf = new RandomAccessFile(file, "r");
    this.buffer =
        raf.getChannel()
            .map(FileChannel.MapMode.READ_ONLY, 0, Math.min(file.length(), Integer.MAX_VALUE));
  }

  public MappedByteBuffer getBuffer() {
    return buffer;
  }

  @Override
  public void close() {
    buffer = null;
    if (raf != null) {
      try {
        raf.close();
      } catch (Exception ignored) {
        // ignore
      }
    }
  }
}
