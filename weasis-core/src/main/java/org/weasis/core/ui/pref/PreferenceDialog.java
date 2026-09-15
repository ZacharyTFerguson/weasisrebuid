/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.weasis.core.api.gui.PreferencesPageFactory;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.UICore;

/** File &gt; Preferences (Alt+P) shell. */
public class PreferenceDialog extends JDialog {

  private final List<AbstractItemDialogPage> pages;
  private AbstractItemDialogPage current;
  private JButton okButton;

  public PreferenceDialog(Window parent) {
    this(parent, instantiatePages(UICore.getInstance().getPreferencesPageFactories()));
  }

  public PreferenceDialog(Window parent, List<AbstractItemDialogPage> pages) {
    super(parent, "Preferences");
    this.pages = pages == null ? List.of() : List.copyOf(pages);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    setModal(true);
    build();
  }

  public static List<AbstractItemDialogPage> instantiatePages(
      List<PreferencesPageFactory> factories) {
    Hashtable<String, Object> props = new Hashtable<>();
    List<AbstractItemDialogPage> result = new ArrayList<>();
    if (factories != null) {
      for (PreferencesPageFactory factory : factories) {
        AbstractItemDialogPage page = factory.createInstance(props);
        if (page != null) {
          result.add(page);
        }
      }
    }
    result.sort(Comparator.comparingInt(AbstractItemDialogPage::getComponentPosition));
    return result;
  }

  public List<AbstractItemDialogPage> getPages() {
    return pages;
  }

  public AbstractItemDialogPage getCurrentPage() {
    return current;
  }

  public void selectPage(AbstractItemDialogPage page) {
    this.current = page;
  }

  public void applyAndClose() {
    commitOpenEdits(getContentPane());
    for (AbstractItemDialogPage page : pages) {
      closeTree(page);
    }
    dispose();
  }

  JButton okButton() {
    return okButton;
  }

  static void commitOpenEdits(Component root) {
    if (commitSpinnerOrField(root)) {
      return;
    }
    commitChildEdits(root);
  }

  static boolean commitSpinnerOrField(Component root) {
    if (root instanceof JSpinner spinner) {
      commitSpinner(spinner);
      return true;
    }
    if (root instanceof JFormattedTextField field) {
      commitField(field);
      return true;
    }
    return false;
  }

  static void commitChildEdits(Component root) {
    if (!(root instanceof Container container)) {
      return;
    }
    for (Component child : container.getComponents()) {
      commitOpenEdits(child);
    }
  }

  static void commitSpinner(JSpinner spinner) {
    try {
      spinner.commitEdit();
    } catch (ParseException ignored) {
      // page still reads the editor text
    }
  }

  static void commitField(JFormattedTextField field) {
    try {
      field.commitEdit();
    } catch (ParseException ignored) {
      // page still reads the editor text
    }
  }

  public void resetCurrent() {
    if (current != null) {
      current.resetToDefaultValues();
    }
  }

  private static void closeTree(AbstractItemDialogPage page) {
    page.closeAdditionalWindow();
    for (AbstractItemDialogPage sub : page.getSubPages()) {
      closeTree(sub);
    }
  }

  private void build() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Preferences");
    for (AbstractItemDialogPage page : pages) {
      root.add(toNode(page));
    }
    JTree tree = new JTree(new DefaultTreeModel(root));
    tree.setRootVisible(false);
    JPanel right = new JPanel(new BorderLayout());
    tree.addTreeSelectionListener(
        e -> {
          Object last = tree.getLastSelectedPathComponent();
          if (last instanceof DefaultMutableTreeNode node
              && node.getUserObject() instanceof AbstractItemDialogPage page) {
            right.removeAll();
            right.add(page, BorderLayout.CENTER);
            current = page;
            right.revalidate();
            right.repaint();
          }
        });
    if (!pages.isEmpty()) {
      current = pages.getFirst();
      right.add(current, BorderLayout.CENTER);
    }
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), right);
    split.setDividerLocation(220);
    JPanel buttons = new JPanel();
    okButton = new JButton("OK");
    okButton.addActionListener(e -> applyAndClose());
    JButton cancel = new JButton("Cancel");
    cancel.addActionListener(e -> dispose());
    JButton reset = new JButton("Restore defaults");
    reset.addActionListener(e -> resetCurrent());
    buttons.add(okButton);
    buttons.add(cancel);
    buttons.add(reset);
    add(split, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(new Dimension(720, 480));
  }

  private static DefaultMutableTreeNode toNode(AbstractItemDialogPage page) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(page);
    for (AbstractItemDialogPage sub : page.getSubPages()) {
      node.add(toNode(sub));
    }
    return node;
  }
}
