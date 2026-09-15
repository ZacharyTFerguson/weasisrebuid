/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.model.list;

import javax.swing.AbstractListModel;
import org.weasis.acquire.explorer.core.ItemList;

/** Swing list model over {@link ItemList}. */
public class ItemListModel<T> extends AbstractListModel<T> {

  protected final ItemList<T> itemList;

  public ItemListModel() {
    this(new ItemList<>());
  }

  public ItemListModel(ItemList<T> itemList) {
    this.itemList = itemList == null ? new ItemList<>() : itemList;
    this.itemList.addListener(
        new ItemList.Listener<>() {
          @Override
          public void itemAdded(T item, int index) {
            fireIntervalAdded(ItemListModel.this, index, index);
          }

          @Override
          public void itemRemoved(T item, int index) {
            fireIntervalRemoved(ItemListModel.this, index, index);
          }

          @Override
          public void currentChanged(T item, int index) {
            int last = getSize() - 1;
            if (last >= 0) {
              fireContentsChanged(ItemListModel.this, 0, last);
            }
          }
        });
  }

  public ItemList<T> itemList() {
    return itemList;
  }

  @Override
  public int getSize() {
    return itemList.size();
  }

  @Override
  public T getElementAt(int index) {
    return itemList.getItem(index);
  }
}
