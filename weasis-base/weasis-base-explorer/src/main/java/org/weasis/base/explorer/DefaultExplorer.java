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

import java.beans.PropertyChangeListener;
import org.weasis.base.explorer.list.impl.DefaultThumbnailList;
import org.weasis.base.explorer.list.impl.JIThumbnailListPane;
import org.weasis.core.api.explorer.DataExplorerView;
import org.weasis.core.api.explorer.ObservableEvent;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.ui.docking.PluginTool;

/** Non-DICOM media explorer used with {@code non-dicom-explorer.json}. */
public class DefaultExplorer extends PluginTool implements DataExplorerView {

  public static final String NAME = "Media Explorer";

  private final JIThumbnailListPane thumbnails =
      new JIThumbnailListPane(new DefaultThumbnailList());
  private final JIExplorerContext explorerContext = JIUtility.newContext();

  private final DataExplorerModel model =
      new DataExplorerModel() {
        @Override
        public Codec[] getCodecPlugins() {
          return new Codec[0];
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {}

        @Override
        public void firePropertyChange(ObservableEvent event) {}
      };

  public DefaultExplorer() {
    super(NAME, 10);
  }

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.EXPLORER;
  }

  @Override
  public DataExplorerModel getDataExplorerModel() {
    return model;
  }

  public JIThumbnailListPane thumbnailPane() {
    return thumbnails;
  }

  public JIExplorerContext explorerContext() {
    return explorerContext;
  }

  @Override
  public void dispose() {
    closeDockable();
  }
}
