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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.media.DicomDirReader;
import org.weasis.dicom.codec.DicomMediaIO;

/** Folder recurse, DICOMDIR, ZIP (incl. password). */
public final class LoadLocalDicom {

  private LoadLocalDicom() {}

  public static ImportResult importPath(
      File path, String zipPassword, DicomModel model, SkipUnsupportedSopNotifier skip)
      throws IOException {
    DragDropRules.Kind kind = DragDropRules.classify(path);
    return switch (kind) {
      case DICOMDIR -> importDicomDir(path, model, skip);
      case ZIP -> importZip(path, zipPassword, model, skip);
      case FOLDER -> importFolder(path, model, skip);
      case DICOM_FILE -> importFile(path, model, skip);
      case REJECT -> new ImportResult(List.of(), List.of(path.getName() + " rejected"));
    };
  }

  public static ImportResult importFile(
      File file, DicomModel model, SkipUnsupportedSopNotifier skip) throws IOException {
    ImportedInstance inst = readInstance(file);
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    if (inst == null) {
      errors.add("corrupt " + file.getName());
      return new ImportResult(ok, errors);
    }
    if (skip != null && !skip.offer(inst)) {
      return new ImportResult(ok, errors);
    }
    if (model != null) {
      model.addInstance(inst);
    }
    ok.add(inst);
    return new ImportResult(ok, errors);
  }

  public static ImportResult importFolder(
      File folder, DicomModel model, SkipUnsupportedSopNotifier skip) throws IOException {
    File dicomdir = new File(folder, "DICOMDIR");
    if (dicomdir.isFile()) {
      return importDicomDir(dicomdir, model, skip);
    }
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(folder.toPath())) {
      List<File> files =
          walk.filter(Files::isRegularFile)
              .map(Path::toFile)
              .filter(f -> DragDropRules.classify(f) == DragDropRules.Kind.DICOM_FILE)
              .toList();
      for (File f : files) {
        ImportResult one = importFile(f, model, skip);
        ok.addAll(one.imported());
        errors.addAll(one.errors());
      }
    }
    return new ImportResult(ok, errors);
  }

  public static ImportResult importZip(
      File zip, String password, DicomModel model, SkipUnsupportedSopNotifier skip)
      throws IOException {
    Path tmp = Files.createTempDirectory("weasis-zip-");
    tmp.toFile().deleteOnExit();
    try (ZipFile zf = openZip(zip, password)) {
      zf.extractAll(tmp.toString());
    } catch (ZipException e) {
      throw new IOException("ZIP (password?) " + zip.getName(), e);
    }
    return importFolder(tmp.toFile(), model, skip);
  }

  static ZipFile openZip(File zip, String password) {
    if (password == null || password.isEmpty()) {
      return new ZipFile(zip);
    }
    return new ZipFile(zip, password.toCharArray());
  }

  public static ImportResult importDicomDir(
      File dicomdir, DicomModel model, SkipUnsupportedSopNotifier skip) throws IOException {
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    File root = dicomdir.getParentFile() == null ? new File(".") : dicomdir.getParentFile();
    try (DicomDirReader reader = new DicomDirReader(dicomdir)) {
      Attributes rec = reader.findFirstRootDirectoryRecordInUse(false);
      walkRecords(reader, rec, root, model, skip, ok, errors);
    }
    if (ok.isEmpty()) {
      return importFolderWithoutDicomdir(root, dicomdir, model, skip);
    }
    return new ImportResult(ok, errors);
  }

  static ImportResult importFolderWithoutDicomdir(
      File root, File dicomdir, DicomModel model, SkipUnsupportedSopNotifier skip)
      throws IOException {
    List<ImportedInstance> ok = new ArrayList<>();
    List<String> errors = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(root.toPath())) {
      List<File> files =
          walk.filter(Files::isRegularFile)
              .map(Path::toFile)
              .filter(f -> !f.getName().equalsIgnoreCase("DICOMDIR"))
              .filter(f -> DragDropRules.classify(f) == DragDropRules.Kind.DICOM_FILE)
              .toList();
      for (File f : files) {
        ImportResult one = importFile(f, model, skip);
        ok.addAll(one.imported());
        errors.addAll(one.errors());
      }
    }
    return new ImportResult(ok, errors);
  }

  static void walkRecords(
      DicomDirReader reader,
      Attributes rec,
      File root,
      DicomModel model,
      SkipUnsupportedSopNotifier skip,
      List<ImportedInstance> ok,
      List<String> errors)
      throws IOException {
    while (rec != null) {
      String type = rec.getString(Tag.DirectoryRecordType, "");
      if ("IMAGE".equalsIgnoreCase(type) || "ENCAP DOC".equalsIgnoreCase(type)) {
        String[] ids = rec.getStrings(Tag.ReferencedFileID);
        File ref = ids == null || ids.length == 0 ? null : reader.toFile(ids);
        if (ref == null || !ref.isFile()) {
          ref = resolveBeside(root, ids);
        }
        if (ref != null && ref.isFile()) {
          ImportResult one = importFile(ref, model, skip);
          ok.addAll(one.imported());
          errors.addAll(one.errors());
        }
      }
      Attributes child = reader.findLowerDirectoryRecordInUse(rec, false);
      walkRecords(reader, child, root, model, skip, ok, errors);
      rec = reader.findNextDirectoryRecordInUse(rec, false);
    }
  }

  static File resolveBeside(File root, String[] ids) {
    if (ids == null || ids.length == 0) {
      return null;
    }
    File cur = root;
    for (String id : ids) {
      cur = new File(cur, id);
    }
    return cur;
  }

  static ImportedInstance readInstance(File file) {
    try {
      DicomMediaIO io = DicomMediaIO.open(file);
      Attributes dcm = io.getDataset();
      return new ImportedInstance(
          dcm.getString(Tag.PatientName, ""),
          dcm.getString(Tag.PatientID, ""),
          dcm.getString(Tag.StudyInstanceUID, ""),
          dcm.getString(Tag.SeriesInstanceUID, ""),
          dcm.getString(Tag.SOPInstanceUID, ""),
          dcm.getString(Tag.SOPClassUID, ""),
          dcm.getString(Tag.Modality, ""),
          dcm.getString(Tag.SeriesDescription, ""),
          dcm.getString(Tag.StudyDate, ""),
          dcm.getInt(Tag.SeriesNumber, 0),
          dcm.getInt(Tag.InstanceNumber, 0),
          file,
          io.mimeType());
    } catch (Exception e) {
      return null;
    }
  }

  public record ImportResult(List<ImportedInstance> imported, List<String> errors) {
    public boolean hasCorrupt() {
      return errors.stream().anyMatch(s -> s.toLowerCase(Locale.ROOT).contains("corrupt"));
    }
  }
}
