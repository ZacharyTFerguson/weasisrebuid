/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.ui.docking.PluginTool;

/** Dicomizer explorer workspace. */
public class AcquireExplorer extends PluginTool implements DataExplorerView {

  public static final String NAME = "Dicomizer";

  private final AcquireManager manager = new AcquireManager();
  private final AcquireExplorerModel model = new AcquireExplorerModel();

  public AcquireExplorer() {
    super(NAME, 20);
  }

  public AcquireManager getManager() {
    return manager;
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
