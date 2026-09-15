/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer;

/** Annotation overlay state. SHORTCUTS.md: Space/I cycle three states (full → minimal → hidden). */
public class AbstractInfoLayer {

  public enum Visibility {
    FULL,
    MINIMAL,
    HIDDEN
  }

  private Visibility visibility = Visibility.FULL;

  public Visibility getVisibility() {
    return visibility;
  }

  public void setVisibility(Visibility visibility) {
    this.visibility = visibility == null ? Visibility.FULL : visibility;
  }

  public void cycle() {
    visibility =
        switch (visibility) {
          case FULL -> Visibility.MINIMAL;
          case MINIMAL -> Visibility.HIDDEN;
          case HIDDEN -> Visibility.FULL;
        };
  }

  public boolean isVisible() {
    return visibility != Visibility.HIDDEN;
  }

  public void setVisible(boolean visible) {
    setVisibility(visible ? Visibility.FULL : Visibility.HIDDEN);
  }

  public boolean isFull() {
    return visibility == Visibility.FULL;
  }

  public boolean isMinimal() {
    return visibility == Visibility.MINIMAL;
  }
}
