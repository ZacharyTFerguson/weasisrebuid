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

/** Maps this JVM to a Weasis {@code native.library.spec} token. */
public final class NativeLibrary {

  private NativeLibrary() {}

  public static String spec() {
    String os = System.getProperty("os.name", "").toLowerCase();
    String arch = System.getProperty("os.arch", "").toLowerCase();
    String family;
    if (os.contains("win")) {
      family = "windows";
    } else if (os.contains("mac") || os.contains("darwin")) {
      family = "macosx";
    } else {
      family = "linux";
    }
    String cpu;
    if (arch.contains("aarch64") || arch.contains("arm64")) {
      cpu = "aarch64";
    } else {
      cpu = "x86-64";
    }
    return family + "-" + cpu;
  }
}
