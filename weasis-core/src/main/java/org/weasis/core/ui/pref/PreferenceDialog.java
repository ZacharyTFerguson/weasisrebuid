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
import java.awt.Dialog;
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
  private JTree tree;
  private JButton okButton;
  private JButton cancelButton;
  private JButton resetButton;

  public PreferenceDialog(Window parent) {
    this(parent, catalogPages());
  }

  static List<AbstractItemDialogPage> catalogPages() {
    DefaultPrefBootstrap.ensureRegistered(UICore.getInstance());
    return instantiatePages(UICore.getInstance().getPreferencesPageFactories());
  }

  public PreferenceDialog(Window parent, List<AbstractItemDialogPage> pages) {
    super(parent, "Preferences", Dialog.ModalityType.MODELESS);
    this.pages = pages == null ? List.of() : List.copyOf(pages);
    setName("preferences");
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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

  public JButton okButton() {
    return okButton;
  }

  public JButton cancelButton() {
    return cancelButton;
  }

  public JButton resetButton() {
    return resetButton;
  }

  public JTree tree() {
    return tree;
  }

  public List<String> pageTitles() {
    List<String> titles = new ArrayList<>();
    for (AbstractItemDialogPage page : pages) {
      titles.add(page.getTitle());
    }
    return titles;
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
    JPanel right = pageHost();
    tree = new JTree(new DefaultTreeModel(rootNode()));
    tree.setName("pref-tree");
    tree.setRootVisible(false);
    bindTree(right);
    if (!pages.isEmpty()) {
      current = pages.getFirst();
      right.add(current, BorderLayout.CENTER);
    }
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), right);
    split.setName("pref-split");
    split.setDividerLocation(220);
    add(split, BorderLayout.CENTER);
    add(buttonBar(), BorderLayout.SOUTH);
    setSize(new Dimension(720, 480));
  }

  JPanel pageHost() {
    JPanel right = new JPanel(new BorderLayout());
    right.setName("pref-page");
    return right;
  }

  DefaultMutableTreeNode rootNode() {
    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Preferences");
    for (AbstractItemDialogPage page : pages) {
      root.add(toNode(page));
    }
    return root;
  }

  void bindTree(JPanel right) {
    tree.addTreeSelectionListener(
        e -> {
          Object last = tree.getLastSelectedPathComponent();
          if (last instanceof DefaultMutableTreeNode node
              && node.getUserObject() instanceof AbstractItemDialogPage page) {
            showPage(right, page);
          }
        });
  }

  void showPage(JPanel right, AbstractItemDialogPage page) {
    right.removeAll();
    right.add(page, BorderLayout.CENTER);
    current = page;
    right.revalidate();
    right.repaint();
  }

  JPanel buttonBar() {
    okButton = new JButton("OK");
    okButton.setName("pref-ok");
    okButton.addActionListener(e -> applyAndClose());
    cancelButton = new JButton("Cancel");
    cancelButton.setName("pref-cancel");
    cancelButton.addActionListener(e -> dispose());
    resetButton = new JButton("Restore defaults");
    resetButton.setName("pref-reset");
    resetButton.addActionListener(e -> resetCurrent());
    JPanel buttons = new JPanel();
    buttons.setName("pref-buttons");
    buttons.add(okButton);
    buttons.add(cancelButton);
    buttons.add(resetButton);
    return buttons;
  }

  private static DefaultMutableTreeNode toNode(AbstractItemDialogPage page) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(page);
    for (AbstractItemDialogPage sub : page.getSubPages()) {
      node.add(toNode(sub));
    }
    return node;
  }
}
