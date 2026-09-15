/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.exp;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Hashtable;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.weasis.dicom.explorer.DicomModel;

/** File &gt; Export DICOM dialog: check tree plus local / ZIP / DICOMDIR pages. */
public class ExportDicomView extends JDialog {

  private final DicomModel model;
  private final CheckTreeModel treeModel;
  private final ExportTree tree;
  private final JTabbedPane tabs = new JTabbedPane();

  public ExportDicomView(Frame owner, DicomModel model) {
    super(owner, "Export DICOM", true);
    this.model = model == null ? new DicomModel() : model;
    this.treeModel = new CheckTreeModel(this.model);
    this.tree = new ExportTree(treeModel);
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("model", this.model);
    DicomExportFactory factory = new DicomExportFactory();
    tabs.add(DicomExportFactory.PAGE_LOCAL, page(factory, props, DicomExportFactory.PAGE_LOCAL));
    tabs.add(DicomExportFactory.PAGE_ZIP, page(factory, props, DicomExportFactory.PAGE_ZIP));
    tabs.add(DicomExportFactory.PAGE_DIR, page(factory, props, DicomExportFactory.PAGE_DIR));
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(tree), tabs);
    split.setDividerLocation(220);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(split, BorderLayout.CENTER);
    setSize(720, 420);
  }

  static LocalExport page(
      DicomExportFactory factory, Hashtable<String, Object> base, String title) {
    Hashtable<String, Object> props = new Hashtable<>(base);
    props.put("title", title);
    return (LocalExport) factory.createDicomExportPage(props);
  }

  public DicomModel getModel() {
    return model;
  }

  public CheckTreeModel getCheckTreeModel() {
    return treeModel;
  }

  public ExportTree getExportTree() {
    return tree;
  }

  public LocalExport page(String title) {
    for (int i = 0; i < tabs.getTabCount(); i++) {
      if (title.equals(tabs.getTitleAt(i)) && tabs.getComponentAt(i) instanceof LocalExport page) {
        return page;
      }
    }
    return null;
  }
}
