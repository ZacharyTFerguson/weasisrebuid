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

import javax.swing.ComboBoxModel;
import org.weasis.acquire.explorer.core.ItemList;

/** Combo model whose selected item is {@link ItemList#getCurrentItem()}. */
public class ItemListComboBoxModel<T> extends ItemListModel<T> implements ComboBoxModel<T> {

  public ItemListComboBoxModel() {
    super();
  }

  public ItemListComboBoxModel(ItemList<T> itemList) {
    super(itemList);
  }

  @Override
  public void setSelectedItem(Object anItem) {
    @SuppressWarnings("unchecked")
    T item = (T) anItem;
    itemList.setCurrentItem(item);
  }

  @Override
  public Object getSelectedItem() {
    return itemList.getCurrentItem();
  }
}
