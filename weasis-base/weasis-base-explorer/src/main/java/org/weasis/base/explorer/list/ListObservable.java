/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Notifies listeners when a thumbnail / disk file list changes. */
public class ListObservable {

  public static final String ITEMS = "items";

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);
  private int generation;

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  public void fireListChanged() {
    int previous = generation;
    generation++;
    support.firePropertyChange(ITEMS, previous, generation);
  }

  public int generation() {
    return generation;
  }
}
