/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/** Selected 2D view for Gogo {@code dcmview2d:*} (not a shared mutable test fixture). */
public final class View2dRegistry {

  private static final CopyOnWriteArrayList<View2d> VIEWS = new CopyOnWriteArrayList<>();
  private static final AtomicReference<View2d> SELECTED = new AtomicReference<>();

  private View2dRegistry() {}

  public static void register(View2d view) {
    if (view != null && !VIEWS.contains(view)) {
      VIEWS.add(view);
    }
  }

  public static void unregister(View2d view) {
    VIEWS.remove(view);
    SELECTED.compareAndSet(view, VIEWS.isEmpty() ? null : VIEWS.getFirst());
  }

  public static void select(View2d view) {
    if (view != null) {
      register(view);
      SELECTED.set(view);
    }
  }

  public static View2d selected() {
    View2d v = SELECTED.get();
    if (v != null) {
      return v;
    }
    return VIEWS.isEmpty() ? null : VIEWS.getFirst();
  }
}
