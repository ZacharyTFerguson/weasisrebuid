/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/** Ordered items plus a current selection, used by browse combo models. */
public class ItemList<T> {

  public interface Listener<T> {
    default void itemAdded(T item, int index) {}

    default void itemRemoved(T item, int index) {}

    default void currentChanged(T item, int index) {}
  }

  private final List<T> items = new ArrayList<>();
  private final List<Listener<T>> listeners = new CopyOnWriteArrayList<>();
  private int current = -1;

  public void addListener(Listener<T> listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  public void removeListener(Listener<T> listener) {
    listeners.remove(listener);
  }

  public void addItem(T item) {
    if (item == null) {
      return;
    }
    items.add(item);
    int index = items.size() - 1;
    for (Listener<T> listener : listeners) {
      listener.itemAdded(item, index);
    }
    if (current < 0) {
      setCurrentIndex(0);
    }
  }

  public void removeItem(T item) {
    int index = items.indexOf(item);
    if (index < 0) {
      return;
    }
    items.remove(index);
    for (Listener<T> listener : listeners) {
      listener.itemRemoved(item, index);
    }
    if (items.isEmpty()) {
      current = -1;
      fireCurrent(null, -1);
    } else if (current >= items.size()) {
      setCurrentIndex(items.size() - 1);
    } else if (current == index) {
      fireCurrent(items.get(current), current);
    } else if (current > index) {
      current--;
    }
  }

  public T getItem(int index) {
    return items.get(index);
  }

  public int size() {
    return items.size();
  }

  public List<T> getItems() {
    return Collections.unmodifiableList(items);
  }

  public int getCurrentIndex() {
    return current;
  }

  public T getCurrentItem() {
    return current < 0 || current >= items.size() ? null : items.get(current);
  }

  public void setCurrentItem(T item) {
    if (item == null) {
      current = -1;
      fireCurrent(null, -1);
      return;
    }
    int index = indexOf(item);
    if (index >= 0) {
      setCurrentIndex(index);
    }
  }

  public void setCurrentIndex(int index) {
    if (index < 0 || index >= items.size()) {
      return;
    }
    if (current == index) {
      return;
    }
    current = index;
    fireCurrent(items.get(index), index);
  }

  int indexOf(T item) {
    for (int i = 0; i < items.size(); i++) {
      if (Objects.equals(items.get(i), item)) {
        return i;
      }
    }
    return -1;
  }

  void fireCurrent(T item, int index) {
    for (Listener<T> listener : listeners) {
      listener.currentChanged(item, index);
    }
  }
}
