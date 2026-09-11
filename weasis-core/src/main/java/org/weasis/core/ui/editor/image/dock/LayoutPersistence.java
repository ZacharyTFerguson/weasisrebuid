/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.dock;

/** Docking layout is not restored after restart. Do not invent persistence. */
public final class LayoutPersistence {

  private LayoutPersistence() {}

  public static DockingLayout loadOnRestart() {
    return new DockingLayout();
  }

  public static void save(DockingLayout layout) {
    // intentionally no-op — Weasis 4.7.3 does not persist docking layout
  }
}
