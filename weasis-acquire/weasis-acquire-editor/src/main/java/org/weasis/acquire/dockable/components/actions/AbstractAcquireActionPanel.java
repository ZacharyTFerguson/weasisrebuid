/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireImageInfo;
import org.weasis.acquire.explorer.AcquireImageValues;

/** Base {@link AcquireActionPanel} that holds the pending image session. */
public class AbstractAcquireActionPanel extends JPanel implements AcquireActionPanel {

  private AcquireImageInfo imageInfo;
  private AcquireImageValues values = new AcquireImageValues();

  public AbstractAcquireActionPanel() {
    super(new BorderLayout());
  }

  @Override
  public void initValues(AcquireImageInfo info, AcquireImageValues values) {
    this.imageInfo = info;
    this.values = values == null ? new AcquireImageValues() : values;
  }

  public AcquireImageInfo getImageInfo() {
    return imageInfo;
  }

  public AcquireImageValues getValues() {
    return values;
  }

  public boolean needValidationPanel() {
    return false;
  }

  public void stopEditing() {}
}
