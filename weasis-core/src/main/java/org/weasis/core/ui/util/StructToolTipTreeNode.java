/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.util;

import javax.swing.tree.DefaultMutableTreeNode;

public class StructToolTipTreeNode extends DefaultMutableTreeNode {
  private final String toolTip;

  public StructToolTipTreeNode(Object userObject, String toolTip) {
    super(userObject);
    this.toolTip = toolTip == null ? "" : toolTip;
  }

  public String getToolTipText() {
    return toolTip;
  }
}
