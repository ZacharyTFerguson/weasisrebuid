/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.viewer2d;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Non-DICOM stills import chrome. Local files go through {@link ImageCommands} {@code image:get -f}
 * (ImageIO); tests call {@link #importFiles} instead of a file chooser.
 */
public class ImportToolBar extends WtoolBar {

  public static final String NAME = "Import images";

  private final ImageCommands commands;

  public ImportToolBar() {
    this(new ImageCommands());
  }

  public ImportToolBar(ImageCommands commands) {
    super(NAME, 5);
    this.commands = commands == null ? new ImageCommands() : commands;
    JButton button = new JButton(NAME);
    button.setName("import-images");
    button.addActionListener(e -> importFiles());
    add(button);
  }

  public ImageCommands commands() {
    return commands;
  }

  public String importFiles(File... files) {
    if (files == null || files.length == 0) {
      return ImageCommands.GET_USAGE;
    }
    List<String> args = new ArrayList<>();
    for (File file : files) {
      if (file != null) {
        args.add("-f");
        args.add(file.getAbsolutePath());
      }
    }
    if (args.isEmpty()) {
      return ImageCommands.GET_USAGE;
    }
    return commands.get(args.toArray(String[]::new));
  }
}
