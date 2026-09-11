/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d;

import org.weasis.core.ui.model.graphic.GraphicKind;

/**
 * Draw &amp; Measure dock: tools A, drawings B (no stats), graphic options C, selected table D.
 * Selection tool first; draw-once vs keep-tool.
 */
public final class DrawMeasureDock {

  public enum Section {
    TOOLS_A,
    DRAWINGS_B,
    OPTIONS_C,
    SELECTED_TABLE_D
  }

  private GraphicKind currentTool = GraphicKind.SELECT;
  private boolean keepTool;
  private boolean showDrawings = true;
  private boolean deleteConfirm = true;

  public GraphicKind currentTool() {
    return currentTool;
  }

  /** Selection tool first; left mouse becomes the picked tool. */
  public void pick(GraphicKind kind) {
    this.currentTool = kind == null ? GraphicKind.SELECT : kind;
  }

  public boolean keepTool() {
    return keepTool;
  }

  public void setKeepTool(boolean keepTool) {
    this.keepTool = keepTool;
  }

  public void afterStroke() {
    if (!keepTool) {
      currentTool = GraphicKind.SELECT;
    }
  }

  public boolean showDrawings() {
    return showDrawings;
  }

  public void setShowDrawings(boolean showDrawings) {
    this.showDrawings = showDrawings;
  }

  public boolean deleteConfirm() {
    return deleteConfirm;
  }

  public void setDeleteConfirm(boolean deleteConfirm) {
    this.deleteConfirm = deleteConfirm;
  }
}
