/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image.dock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Docking layouts: drag-split / merge; Split Right / Split Down / Close / Close Others / Close All;
 * Maximize occupies the entire window including tool strips. Layout is <strong>not</strong>
 * persisted (CHECKLIST §4.1 / WP-6).
 */
public final class DockingLayout {

  public enum Split {
    RIGHT,
    DOWN
  }

  public enum PaneMode {
    DOCKED,
    PINNED_OVERLAY,
    MINIMIZED
  }

  private final List<String> tabs = new ArrayList<>();
  private final Map<String, PaneMode> toolPanes = new LinkedHashMap<>();
  private String maximizedTab;
  private String focusedTab;

  public void openTab(String id) {
    if (id != null && !tabs.contains(id)) {
      tabs.add(id);
    }
    focusedTab = id;
  }

  public List<String> tabs() {
    return List.copyOf(tabs);
  }

  public String focusedTab() {
    return focusedTab;
  }

  public void split(String tabId, Split dir) {
    Objects.requireNonNull(dir, "dir");
    if (tabId == null || !tabs.contains(tabId)) {
      return;
    }
    String created = tabId + "-" + dir.name().toLowerCase();
    if (!tabs.contains(created)) {
      int idx = tabs.indexOf(tabId);
      tabs.add(idx + 1, created);
    }
  }

  /** Split Right / Split Down enabled only when the tab shares its group. */
  public boolean canSplit(String tabId) {
    return tabId != null && tabs.contains(tabId) && tabs.size() >= 1 && maximizedTab == null;
  }

  public void close(String tabId) {
    tabs.remove(tabId);
    if (tabId != null && tabId.equals(maximizedTab)) {
      maximizedTab = null;
    }
    if (tabId != null && tabId.equals(focusedTab)) {
      focusedTab = tabs.isEmpty() ? null : tabs.get(0);
    }
  }

  public void closeOthers(String tabId) {
    tabs.removeIf(t -> !t.equals(tabId));
    focusedTab = tabs.contains(tabId) ? tabId : null;
    if (maximizedTab != null && !tabId.equals(maximizedTab)) {
      maximizedTab = null;
    }
  }

  public void closeAll() {
    tabs.clear();
    focusedTab = null;
    maximizedTab = null;
  }

  /** Maximize occupies the entire window including the tool strips. */
  public void maximize(String tabId) {
    if (tabId != null && tabs.contains(tabId)) {
      maximizedTab = tabId;
      focusedTab = tabId;
    }
  }

  public void restore() {
    maximizedTab = null;
  }

  public boolean isMaximized() {
    return maximizedTab != null;
  }

  public String maximizedTab() {
    return maximizedTab;
  }

  public boolean maximizeCoversToolStrips() {
    return isMaximized();
  }

  public void setToolPaneMode(String paneId, PaneMode mode) {
    if (paneId != null && mode != null) {
      toolPanes.put(paneId, mode);
    }
  }

  public PaneMode toolPaneMode(String paneId) {
    return toolPanes.getOrDefault(paneId, PaneMode.DOCKED);
  }
}
