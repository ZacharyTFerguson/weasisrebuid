/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.prefs.BackingStoreException;

/** File-backed preference store used by the OSGi prefs service. */
public class StreamBackingStoreImpl {
  private final File store;
  private final Properties props = new Properties();

  public StreamBackingStoreImpl(File store) {
    this.store = store;
    load();
  }

  public synchronized void load() {
    if (store == null || !store.isFile()) {
      return;
    }
    try (FileInputStream in = new FileInputStream(store)) {
      props.load(in);
    } catch (Exception ignored) {
      // empty store
    }
  }

  public synchronized void flush() throws BackingStoreException {
    if (store == null) {
      return;
    }
    try {
      File parent = store.getParentFile();
      if (parent != null) {
        parent.mkdirs();
      }
      try (FileOutputStream out = new FileOutputStream(store)) {
        props.store(out, "weasis-prefs");
      }
    } catch (Exception e) {
      throw new BackingStoreException(e.toString());
    }
  }

  public synchronized String get(String key, String def) {
    return props.getProperty(key, def);
  }

  public synchronized void put(String key, String value) {
    if (key == null) {
      return;
    }
    if (value == null) {
      props.remove(key);
    } else {
      props.setProperty(key, value);
    }
  }
}
