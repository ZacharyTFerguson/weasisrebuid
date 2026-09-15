/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.gui.util;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public abstract class ComboItemListener<T> extends BasicActionState {
  private final ComboBoxModel<T> model;

  protected ComboItemListener(Feature<?> action, T[] items) {
    super(action);
    this.model = items == null ? new ComboBoxModelAdapter<>() : new ComboBoxModelAdapter<>(items);
    this.model.addListDataListener(
        new ListDataListener() {
          @Override
          public void intervalAdded(ListDataEvent e) {}

          @Override
          public void intervalRemoved(ListDataEvent e) {}

          @Override
          public void contentsChanged(ListDataEvent e) {
            itemStateChanged(getSelectedItem());
          }
        });
  }

  public ComboBoxModel<T> getModel() {
    return model;
  }

  public T getSelectedItem() {
    @SuppressWarnings("unchecked")
    T item = (T) model.getSelectedItem();
    return item;
  }

  public void setSelectedItem(T item) {
    model.setSelectedItem(item);
  }

  public abstract void itemStateChanged(T item);
}
