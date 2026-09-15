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

public class ChunkedArray {
  private final int chunkSize;
  private byte[][] chunks = new byte[0][];

  public ChunkedArray(int chunkSize) {
    this.chunkSize = Math.max(1, chunkSize);
  }

  public int getChunkSize() {
    return chunkSize;
  }

  public int size() {
    return chunks.length;
  }

  public void setChunks(byte[][] chunks) {
    this.chunks = chunks == null ? new byte[0][] : chunks;
  }

  public byte[] getChunk(int index) {
    if (index < 0 || index >= chunks.length) {
      return new byte[0];
    }
    return chunks[index];
  }
}
