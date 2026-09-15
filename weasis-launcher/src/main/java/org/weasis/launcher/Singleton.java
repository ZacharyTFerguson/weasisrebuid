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

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Singleton {
  private static FileChannel channel;
  private static FileLock lock;

  private Singleton() {}

  public static boolean tryLock(Path weasisHome) {
    try {
      Files.createDirectories(weasisHome);
      Path lockFile = weasisHome.resolve(".lock");
      channel = FileChannel.open(
          lockFile,
          java.nio.file.StandardOpenOption.CREATE,
          java.nio.file.StandardOpenOption.WRITE);
      lock = channel.tryLock();
      return lock != null;
    } catch (Exception e) {
      return false;
    }
  }

  public static void release() {
    try {
      if (lock != null) {
        lock.release();
      }
      if (channel != null) {
        channel.close();
      }
    } catch (Exception ignored) {
      // ignore
    }
  }
}
