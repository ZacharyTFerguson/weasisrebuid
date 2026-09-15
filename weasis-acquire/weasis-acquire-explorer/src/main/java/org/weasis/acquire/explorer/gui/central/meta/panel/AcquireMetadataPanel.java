/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.central.meta.panel;

import java.awt.BorderLayout;
import java.util.Objects;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireMeta;
import org.weasis.acquire.explorer.gui.central.meta.model.AcquireMetadataTableModel;

/** Table chrome for one acquire metadata scope (global / series / image). */
public class AcquireMetadataPanel extends JPanel {

  private final AcquireMetadataTableModel model;

  public AcquireMetadataPanel(AcquireMetadataTableModel model) {
    super(new BorderLayout());
    this.model = Objects.requireNonNull(model, "model");
  }

  public AcquireMetadataTableModel model() {
    return model;
  }

  public AcquireMeta.Scope scope() {
    return model.scope();
  }

  public boolean requiredComplete() {
    return model.requiredComplete();
  }
}
