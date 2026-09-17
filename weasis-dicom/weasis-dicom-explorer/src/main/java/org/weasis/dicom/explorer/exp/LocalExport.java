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
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;
import org.dcm4che3.media.DicomDirWriter;
import org.dcm4che3.media.RecordFactory;
import org.dcm4che3.util.UIDUtils;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.dicom.codec.DicomMediaIO;
import org.weasis.dicom.explorer.DicomModel;
import org.weasis.dicom.explorer.ImportedInstance;

/** File &gt; Export DICOM local files / ZIP / DICOMDIR page. */
public class LocalExport extends AbstractItemDialogPage implements ExportDicom {

  private final DicomModel model;
  private final JTextField pathField = new JTextField();
  private final JPasswordField passwordField = new JPasswordField();
  private final JButton browse = new JButton("Browse…");
  private final JButton exportBtn = new JButton("Export");
  private final JLabel status = new JLabel(" ");

  public LocalExport(String title, DicomModel model) {
    super(title == null || title.isBlank() ? DicomExportFactory.PAGE_LOCAL : title, 0);
    this.model = model == null ? new DicomModel() : model;
    pathField.setName("export-path");
    passwordField.setName("export-zip-password");
    browse.setName("export-browse");
    exportBtn.setName("export-run");
    status.setName("export-status");
    JPanel form = new JPanel(new GridLayout(0, 1, 4, 4));
    form.add(new JLabel(getTitle() + " — destination folder, ZIP, or DICOMDIR"));
    JPanel pathRow = new JPanel(new BorderLayout(4, 0));
    pathRow.add(pathField, BorderLayout.CENTER);
    browse.addActionListener(e -> browse());
    pathRow.add(browse, BorderLayout.EAST);
    form.add(pathRow);
    form.add(new JLabel("ZIP password (optional)"));
    form.add(passwordField);
    exportBtn.addActionListener(e -> runExport());
    form.add(exportBtn);
    form.add(status);
    add(form, BorderLayout.NORTH);
  }

  public JTextField pathField() {
    return pathField;
  }

  public JPasswordField passwordField() {
    return passwordField;
  }

  public JButton exportButton() {
    return exportBtn;
  }

  public JLabel statusLabel() {
    return status;
  }

  public DicomModel getModel() {
    return model;
  }

  void browse() {
    JFileChooser chooser = new JFileChooser();
    boolean zip = DicomExportFactory.PAGE_ZIP.equals(getTitle());
    chooser.setFileSelectionMode(zip ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
    chooser.setDialogTitle(getTitle());
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      File selected = chooser.getSelectedFile();
      if (selected != null) {
        pathField.setText(selected.getAbsolutePath());
      }
    }
  }

  void runExport() {
    String path = pathField.getText();
    if (path == null || path.isBlank()) {
      status.setText("Choose a destination");
      return;
    }
    try {
      File dest = exportDICOM(new CheckTreeModel(model), new File(path));
      status.setText("Exported " + dest.getAbsolutePath());
    } catch (IOException e) {
      status.setText("Error " + e.getMessage());
    }
  }

  @Override
  public File exportDICOM(CheckTreeModel tree, File dest) throws IOException {
    List<ImportedInstance> selected =
        tree == null ? model.getInstances() : tree.selectedInstances();
    return exportInstances(selected, dest, new String(passwordField.getPassword()));
  }

  public File exportInstances(List<ImportedInstance> instances, File dest, String zipPassword)
      throws IOException {
    if (dest == null) {
      throw new IOException("export dest");
    }
    List<ImportedInstance> list = instances == null ? List.of() : instances;
    if (DicomExportFactory.PAGE_ZIP.equals(getTitle())) {
      return writeZip(list, dest, zipPassword);
    }
    if (DicomExportFactory.PAGE_DIR.equals(getTitle())) {
      return writeDicomDir(list, dest);
    }
    return writeFiles(list, dest);
  }

  static File writeFiles(List<ImportedInstance> instances, File destDir) throws IOException {
    Files.createDirectories(destDir.toPath());
    for (File copied : copyInstances(instances, destDir)) {
      if (!copied.isFile()) {
        throw new IOException("export copy " + copied);
      }
    }
    return destDir;
  }

  static File writeZip(List<ImportedInstance> instances, File zip, String password)
      throws IOException {
    File parent = zip.getParentFile();
    if (parent != null) {
      Files.createDirectories(parent.toPath());
    }
    boolean encrypt = password != null && !password.isEmpty();
    ZipParameters params = new ZipParameters();
    if (encrypt) {
      params.setEncryptFiles(true);
      params.setEncryptionMethod(EncryptionMethod.AES);
    }
    try (ZipFile zf = encrypt ? new ZipFile(zip, password.toCharArray()) : new ZipFile(zip)) {
      for (ImportedInstance inst : instances) {
        if (inst == null || inst.file() == null || !inst.file().isFile()) {
          continue;
        }
        zf.addFile(inst.file(), params);
      }
    }
    return zip;
  }

  static File writeDicomDir(List<ImportedInstance> instances, File destDir) throws IOException {
    Files.createDirectories(destDir.toPath());
    List<File> copied = copyInstances(instances, destDir);
    File dicomdir = new File(destDir, "DICOMDIR");
    String fsUid = UIDUtils.createUID("2.25");
    DicomDirWriter.createEmptyDirectory(dicomdir, fsUid, "WEASIS", null, "ISO_IR 100");
    RecordFactory factory = new RecordFactory();
    factory.loadDefaultConfiguration();
    try (DicomDirWriter writer = DicomDirWriter.open(dicomdir)) {
      for (File file : copied) {
        addDicomDirRecord(writer, factory, file);
      }
      writer.commit();
    }
    return dicomdir;
  }

  static List<File> copyInstances(List<ImportedInstance> instances, File destDir)
      throws IOException {
    List<File> copied = new ArrayList<>();
    int index = 0;
    for (ImportedInstance inst : instances) {
      if (inst == null || inst.file() == null || !inst.file().isFile()) {
        continue;
      }
      String name = safeName(inst.sopUid(), index++);
      File dest = new File(destDir, name);
      Files.copy(inst.file().toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
      copied.add(dest);
    }
    return copied;
  }

  static String safeName(String sopUid, int index) {
    String base = sopUid == null || sopUid.isBlank() ? "IMG" + index : sopUid;
    String cleaned = base.replaceAll("[^A-Za-z0-9._-]", "_");
    if (cleaned.length() > 64) {
      cleaned = cleaned.substring(cleaned.length() - 64);
    }
    return cleaned + ".dcm";
  }

  static void addDicomDirRecord(DicomDirWriter writer, RecordFactory factory, File file)
      throws IOException {
    DicomMediaIO io = DicomMediaIO.open(file);
    Attributes ds = io.getDataset();
    Attributes fmi = synthesizeFmi(ds, io.getTransferSyntax());
    String[] fileIDs = writer.toFileIDs(file);
    Attributes rec = factory.createRecord(ds, fmi, fileIDs);
    Attributes patient = writer.findOrAddPatientRecord(ds);
    Attributes study = writer.findOrAddStudyRecord(patient, ds);
    Attributes series = writer.findOrAddSeriesRecord(study, ds);
    writer.addLowerDirectoryRecord(series, rec);
  }

  static Attributes synthesizeFmi(Attributes ds, String transferSyntax) {
    Attributes fmi = new Attributes();
    fmi.setString(
        Tag.MediaStorageSOPClassUID, VR.UI, ds.getString(Tag.SOPClassUID, UID.CTImageStorage));
    fmi.setString(Tag.MediaStorageSOPInstanceUID, VR.UI, ds.getString(Tag.SOPInstanceUID, ""));
    fmi.setString(
        Tag.TransferSyntaxUID,
        VR.UI,
        transferSyntax == null || transferSyntax.isBlank()
            ? UID.ExplicitVRLittleEndian
            : transferSyntax);
    return fmi;
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
