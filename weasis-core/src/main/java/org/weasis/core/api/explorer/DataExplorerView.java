/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.explorer;

import java.awt.Frame;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.gui.Insertable;

public interface DataExplorerView extends Insertable {

  DataExplorerModel getDataExplorerModel();

  void dispose();

  /** File &gt; Export DICOM. Explorers that own a DICOM model override this. */
  default void openExport(Frame owner) {}

  @Override
  default Type getType() {
    return Type.EXPLORER;
  }
}
