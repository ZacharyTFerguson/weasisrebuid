/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import javax.swing.JLabel;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.docking.PluginTool;

/** Filesystem explorer used by the non-DICOM profile; Dicomizer disables this factory. */
public class DefaultExplorer extends PluginTool implements DataExplorerView {

  public static final String NAME = "Image Explorer";

  private final DefaultExplorerModel model = new DefaultExplorerModel();

  public DefaultExplorer() {
    super(NAME, 0);
    add(new JLabel(NAME));
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.EXPLORER;
  }

  @Override
  public DataExplorerModel getDataExplorerModel() {
    return model;
  }

  @Override
  public void dispose() {
    closeDockable();
  }
}
