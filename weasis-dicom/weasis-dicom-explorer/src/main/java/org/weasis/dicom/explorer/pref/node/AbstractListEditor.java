/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * Add / edit / delete chrome over a {@link JList}. Subclasses supply the item created or edited
 * from their own fields; they must not name a method {@code layout()}.
 */
public abstract class AbstractListEditor<T> extends JPanel {

  public static final String ADD = "add";
  public static final String EDIT = "edit";
  public static final String DELETE = "delete";

  private final DefaultListModel<T> model = new DefaultListModel<>();
  private final JList<T> list = new JList<>(model);
  private final JButton addButton = new JButton("Add");
  private final JButton editButton = new JButton("Edit");
  private final JButton deleteButton = new JButton("Delete");

  protected AbstractListEditor() {
    super(new BorderLayout());
    list.setName("listEditor");
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    addButton.setName(ADD);
    editButton.setName(EDIT);
    deleteButton.setName(DELETE);
    addButton.addActionListener(e -> addContent());
    editButton.addActionListener(e -> modifyContent());
    deleteButton.addActionListener(e -> deleteContent());
    JPanel buttons = new JPanel(new GridLayout(0, 1));
    buttons.add(addButton);
    buttons.add(editButton);
    buttons.add(deleteButton);
    add(new JScrollPane(list), BorderLayout.CENTER);
    add(buttons, BorderLayout.EAST);
  }

  /** Build a new list item from editor fields; {@code null} cancels Add. */
  public abstract T createItem();

  /** Build a replacement for {@code current} from editor fields; {@code null} cancels Edit. */
  public abstract T modifyItem(T current);

  public boolean addContent() {
    T item = createItem();
    if (item == null) {
      return false;
    }
    addElement(item);
    return true;
  }

  public boolean modifyContent() {
    T current = selected();
    int index = list.getSelectedIndex();
    if (current == null || index < 0) {
      return false;
    }
    T edited = modifyItem(current);
    if (edited == null) {
      return false;
    }
    model.set(index, edited);
    list.setSelectedIndex(index);
    return true;
  }

  public boolean deleteContent() {
    int index = list.getSelectedIndex();
    if (index < 0) {
      return false;
    }
    model.remove(index);
    if (!model.isEmpty()) {
      list.setSelectedIndex(Math.min(index, model.size() - 1));
    }
    return true;
  }

  public void addElement(T item) {
    if (item != null) {
      model.addElement(item);
      list.setSelectedValue(item, true);
    }
  }

  public void clearItems() {
    model.clear();
  }

  public void select(int index) {
    if (index >= 0 && index < model.size()) {
      list.setSelectedIndex(index);
    }
  }

  public T selected() {
    return list.getSelectedValue();
  }

  public List<T> items() {
    List<T> out = new ArrayList<>();
    for (int i = 0; i < model.size(); i++) {
      out.add(model.getElementAt(i));
    }
    return List.copyOf(out);
  }

  public DefaultListModel<T> model() {
    return model;
  }

  public JList<T> itemList() {
    return list;
  }

  public JButton addButton() {
    return addButton;
  }

  public JButton editButton() {
    return editButton;
  }

  public JButton deleteButton() {
    return deleteButton;
  }
}
