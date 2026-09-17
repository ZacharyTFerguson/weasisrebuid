/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.isowriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** ISO9660 DICOM CD/DVD export planner (writes a manifest file in tests; no optical burn). */
public class IsoImageExport {

  public static final String STATE = "iso-state";
  public static final String NONE = "none";
  public static final String MANIFEST = "manifest";
  public static final String DICOMDIR = "DICOMDIR";

  private final List<Path> sourceFiles = new ArrayList<>();
  private final JButton write = new JButton("ISO");
  private final JButton dicomdir = new JButton("DICOMDIR");
  private final JLabel state = new JLabel(NONE);
  private boolean includeDicomDir = true;
  private Path lastTarget;
  private Path lastManifest;

  public IsoImageExport() {
    bindIsoChrome();
  }

  void bindIsoChrome() {
    write.setName("iso-write");
    dicomdir.setName("iso-dicomdir");
    state.setName(STATE);
    write.addActionListener(e -> applyWrite());
    dicomdir.addActionListener(e -> applyDicomDir());
  }

  JPanel isoChrome() {
    JPanel chrome = new JPanel();
    chrome.add(write);
    chrome.add(dicomdir);
    chrome.add(state);
    return chrome;
  }

  public boolean isAvailable() {
    return true;
  }

  public void addSource(Path file) {
    sourceFiles.add(file);
  }

  public List<Path> sourceFiles() {
    return List.copyOf(sourceFiles);
  }

  public void setIncludeDicomDir(boolean includeDicomDir) {
    this.includeDicomDir = includeDicomDir;
  }

  public boolean includeDicomDir() {
    return includeDicomDir;
  }

  public void setTarget(Path isoTarget) {
    lastTarget = isoTarget;
  }

  public Path lastManifest() {
    return lastManifest;
  }

  public JButton writeButton() {
    return write;
  }

  public JButton dicomdirButton() {
    return dicomdir;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  /**
   * Prepare export layout: creates parent dirs and writes {@code .weasis-iso-manifest} listing
   * paths (synthetic local fixture).
   */
  public Path writeManifest(Path isoTarget) throws IOException {
    Path parent = isoTarget.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    StringBuilder manifest = new StringBuilder();
    if (includeDicomDir) {
      manifest.append("DICOMDIR\n");
    }
    for (Path path : sourceFiles) {
      manifest.append(path.getFileName()).append('\n');
    }
    Path manifestPath =
        parent == null ? Path.of(".weasis-iso-manifest") : parent.resolve(".weasis-iso-manifest");
    Files.writeString(manifestPath, manifest.toString());
    return manifestPath;
  }

  void applyWrite() {
    Path written = writeQuiet(target());
    if (written != null) {
      lastManifest = written;
      state.setText(MANIFEST);
    }
  }

  void applyDicomDir() {
    setIncludeDicomDir(true);
    Path written = writeQuiet(target());
    if (written != null) {
      lastManifest = written;
      state.setText(DICOMDIR);
    }
  }

  Path target() {
    return lastTarget == null
        ? Path.of(System.getProperty("java.io.tmpdir"), "weasis-iso-out.iso")
        : lastTarget;
  }

  Path writeQuiet(Path isoTarget) {
    try {
      return writeManifest(isoTarget);
    } catch (IOException ex) {
      return null;
    }
  }
}
