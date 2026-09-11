/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.service.UICore;

/** File > Import > DICOM and File > Import > DICOM CD are two entry points. */
public class ImportDicomDialog extends JDialog {

  private final DicomModel model;
  private final boolean cd;

  public ImportDicomDialog(Frame owner, DicomModel model, boolean cd) {
    super(owner, cd ? "Import DICOM CD" : "Import DICOM", true);
    this.model = model == null ? new DicomModel() : model;
    this.cd = cd;
    JTabbedPane tabs = new JTabbedPane();
    Hashtable<String, Object> props = new Hashtable<>();
    props.put("model", this.model);
    if (cd) {
      props.put("title", LocalImportFactory.PAGE_CD);
      tabs.add(LocalImportFactory.PAGE_CD, page(props));
    } else {
      props.put("title", LocalImportFactory.PAGE_LOCAL);
      tabs.add(LocalImportFactory.PAGE_LOCAL, page(props));
      Hashtable<String, Object> zip = new Hashtable<>(props);
      zip.put("title", LocalImportFactory.PAGE_ZIP);
      tabs.add(LocalImportFactory.PAGE_ZIP, page(zip));
      Hashtable<String, Object> dir = new Hashtable<>(props);
      dir.put("title", LocalImportFactory.PAGE_DIR);
      tabs.add(LocalImportFactory.PAGE_DIR, page(dir));
    }
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabs, BorderLayout.CENTER);
    setSize(480, 320);
  }

  static ImportDicomPage page(Hashtable<String, Object> props) {
    ImportDicom created = new LocalImportFactory().createDicomImportPage(props);
    return (ImportDicomPage) created;
  }

  public DicomModel getModel() {
    return model;
  }

  public boolean isCd() {
    return cd;
  }

  public void importFiles(List<File> files, String zipPassword) {
    ImportDicomPage page =
        new ImportDicomPage(
            cd ? LocalImportFactory.PAGE_CD : LocalImportFactory.PAGE_LOCAL,
            0,
            model,
            new SkipUnsupportedSopNotifier());
    page.importFiles(files, zipPassword);
  }

  public static ImportDicomDialog openFromFactories(Frame owner, boolean cd) {
    DicomModel model = new DicomModel();
    if (UICore.getInstance().getDicomImportFactories().isEmpty()) {
      new LocalImportFactory().activate();
    }
    return new ImportDicomDialog(owner, model, cd);
  }
}
