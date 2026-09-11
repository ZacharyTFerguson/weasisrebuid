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
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.weasis.core.api.explorer.ImportDicom;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;

public class ImportDicomPage extends AbstractItemDialogPage implements ImportDicom {

  private final String title;
  private final DicomModel model;
  private final SkipUnsupportedSopNotifier skip;
  private final boolean copyToTemp;
  private final JTextField pathField = new JTextField();
  private final JPasswordField passwordField = new JPasswordField();
  private final JLabel status = new JLabel(" ");
  private final JCheckBox dontShow = new JCheckBox("Don't show again");

  public ImportDicomPage(
      String title, int position, DicomModel model, SkipUnsupportedSopNotifier skip) {
    this(title, position, model, skip, LocalImportFactory.PAGE_CD.equals(title));
  }

  public ImportDicomPage(
      String title,
      int position,
      DicomModel model,
      SkipUnsupportedSopNotifier skip,
      boolean copyToTemp) {
    super(title, position);
    this.title = title;
    this.model = model == null ? new DicomModel() : model;
    this.skip = skip == null ? new SkipUnsupportedSopNotifier() : skip;
    this.copyToTemp = copyToTemp;
    JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
    form.add(new JLabel(title + " — files, folder, ZIP, or DICOMDIR"));
    JPanel pathRow = new JPanel(new BorderLayout(4, 0));
    pathRow.add(pathField, BorderLayout.CENTER);
    JButton browse = new JButton("Browse…");
    browse.addActionListener(e -> browse());
    pathRow.add(browse, BorderLayout.EAST);
    form.add(pathRow);
    form.add(new JLabel("ZIP password (optional)"));
    form.add(passwordField);
    if (copyToTemp) {
      form.add(new JLabel("DICOM CD: copy-to-temp before parse"));
    }
    JButton importBtn = new JButton("Import");
    importBtn.addActionListener(e -> runImport());
    form.add(importBtn);
    dontShow.addActionListener(e -> skip.setDontShowAgain(dontShow.isSelected()));
    form.add(dontShow);
    form.add(status);
    add(form, BorderLayout.NORTH);
  }

  void browse() {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    chooser.setDialogTitle(title);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selected = chooser.getSelectedFile();
      if (selected != null) {
        pathField.setText(selected.getAbsolutePath());
      }
    }
  }

  void runImport() {
    String path = pathField.getText();
    if (path == null || path.isBlank()) {
      status.setText("Choose a file, folder, ZIP, or DICOMDIR");
      return;
    }
    importFiles(List.of(new File(path)), new String(passwordField.getPassword()));
    String info = skip.informationMessage();
    if (!info.isBlank()) {
      status.setText(info);
    } else {
      status.setText("Imported " + model.getInstances().size() + " instance(s)");
    }
    openViewerIfPresent();
  }

  void openViewerIfPresent() {
    if (java.awt.GraphicsEnvironment.isHeadless() || model.getInstances().isEmpty()) {
      return;
    }
    org.weasis.core.api.media.data.Series<org.weasis.core.api.media.data.MediaElement> series =
        new org.weasis.core.api.media.data.Series<>();
    series.setMimeType(org.weasis.dicom.codec.DicomMime.IMAGE_DICOM);
    for (ImportedInstance inst : model.getInstances()) {
      if (inst.file() == null) {
        continue;
      }
      org.weasis.core.api.media.data.MediaElement el =
          new org.weasis.core.api.media.data.MediaElement();
      el.setMediaURI(inst.file().toURI());
      el.setMimeType(org.weasis.dicom.codec.DicomMime.IMAGE_DICOM);
      series.addMedia(el);
    }
    org.weasis.core.api.service.UICore.getInstance()
        .getViewerFactory(org.weasis.dicom.codec.DicomMime.IMAGE_DICOM)
        .ifPresent(
            factory ->
                org.weasis.core.ui.editor.ViewerPluginBuilder.openSequenceInPlugin(
                    factory, series, new java.util.Hashtable<>(), true, true));
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void importFiles(List<File> files, String zipPassword) {
    if (files == null) {
      return;
    }
    for (File file : files) {
      try {
        File src = copyToTemp ? copyLocal(file) : file;
        LoadLocalDicom.importPath(src, zipPassword, model, skip);
      } catch (Exception e) {
        status.setText("Error (corrupt) " + file.getName());
      }
    }
  }

  static File copyLocal(File file) throws Exception {
    if (file == null || !file.exists()) {
      return file;
    }
    Path tmp = Files.createTempDirectory("weasis-cd-");
    tmp.toFile().deleteOnExit();
    if (file.isDirectory()) {
      Path dest = tmp.resolve(file.getName());
      copyTree(file.toPath(), dest);
      return dest.toFile();
    }
    Path dest = tmp.resolve(file.getName());
    Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
    dest.toFile().deleteOnExit();
    return dest.toFile();
  }

  static void copyTree(Path src, Path dest) throws Exception {
    try (var walk = Files.walk(src)) {
      for (Path p : walk.toList()) {
        Path rel = dest.resolve(src.relativize(p).toString());
        if (Files.isDirectory(p)) {
          Files.createDirectories(rel);
        } else {
          Files.createDirectories(rel.getParent());
          Files.copy(p, rel, StandardCopyOption.REPLACE_EXISTING);
          rel.toFile().deleteOnExit();
        }
      }
    }
  }

  public DicomModel getModel() {
    return model;
  }

  public List<ImportedInstance> imported() {
    return new ArrayList<>(model.getInstances());
  }

  @Override
  public void closeAdditionalWindow() {
    // no-op
  }

  @Override
  public void resetToDefaultValues() {
    pathField.setText("");
    passwordField.setText("");
    status.setText(" ");
  }
}
