/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.main;

import java.awt.BorderLayout;
import javax.swing.JPanel;

/** Explorer series thumbnail strip. Selection follows SHORTCUTS.md Explorer. */
public class SeriesPane extends JPanel {

  private final SeriesSelectionModel selection = new SeriesSelectionModel();
  private final ThumbnailMouseAndKeyAdapter adapter = new ThumbnailMouseAndKeyAdapter(selection);

  public SeriesPane() {
    super(new BorderLayout());
  }

  public SeriesSelectionModel getSelectionModel() {
    return selection;
  }

  public ThumbnailMouseAndKeyAdapter getAdapter() {
    return adapter;
  }
}
