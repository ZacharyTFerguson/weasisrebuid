/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;

public class ArrayListComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E> {
  private final List<E> items = new ArrayList<>();
  private E selected;

  public ArrayListComboBoxModel() {}

  public ArrayListComboBoxModel(List<E> items) {
    if (items != null) {
      this.items.addAll(items);
    }
  }

  @Override
  public int getSize() {
    return items.size();
  }

  @Override
  public E getElementAt(int index) {
    return items.get(index);
  }

  @Override
  public void setSelectedItem(Object anItem) {
    @SuppressWarnings("unchecked")
    E e = (E) anItem;
    this.selected = e;
    fireContentsChanged(this, -1, -1);
  }

  @Override
  public Object getSelectedItem() {
    return selected;
  }

  @Override
  public void addElement(E item) {
    items.add(item);
    fireIntervalAdded(this, items.size() - 1, items.size() - 1);
  }

  @Override
  public void removeElement(Object obj) {
    int i = items.indexOf(obj);
    if (i >= 0) {
      removeElementAt(i);
    }
  }

  @Override
  public void insertElementAt(E item, int index) {
    items.add(index, item);
    fireIntervalAdded(this, index, index);
  }

  @Override
  public void removeElementAt(int index) {
    items.remove(index);
    fireIntervalRemoved(this, index, index);
  }
}
