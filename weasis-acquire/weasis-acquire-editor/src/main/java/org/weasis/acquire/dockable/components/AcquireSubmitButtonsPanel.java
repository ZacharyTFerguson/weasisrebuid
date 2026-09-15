/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components;

import javax.swing.JButton;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireImageValues;

/** Apply commits pending photo-editor values; Cancel restores the last committed snapshot. */
public class AcquireSubmitButtonsPanel extends JPanel {

  public static final String APPLY = "Apply";
  public static final String CANCEL = "Cancel";

  private final JButton apply = new JButton(APPLY);
  private final JButton cancel = new JButton(CANCEL);
  private AcquireImageValues pending = new AcquireImageValues();
  private AcquireImageValues snapshot = new AcquireImageValues();

  public AcquireSubmitButtonsPanel() {
    apply.setName("apply");
    cancel.setName("cancel");
    apply.addActionListener(e -> apply());
    cancel.addActionListener(e -> cancel());
    add(apply);
    add(cancel);
  }

  public JButton applyButton() {
    return apply;
  }

  public JButton cancelButton() {
    return cancel;
  }

  public void bind(AcquireImageValues values) {
    this.pending = values == null ? new AcquireImageValues() : values;
    this.snapshot = this.pending.copy();
  }

  public AcquireImageValues pending() {
    return pending;
  }

  public AcquireImageValues snapshot() {
    return snapshot;
  }

  public void apply() {
    snapshot = pending.copy();
  }

  public void cancel() {
    pending.restoreFrom(snapshot);
  }
}
