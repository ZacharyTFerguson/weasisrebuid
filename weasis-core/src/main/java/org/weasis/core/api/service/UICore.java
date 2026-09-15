/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.service;

import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFrame;
import org.osgi.framework.BundleContext;
import org.weasis.core.api.explorer.DataExplorerViewFactory;
import org.weasis.core.api.explorer.DicomImportFactory;
import org.weasis.core.api.gui.InsertableFactory;
import org.weasis.core.api.gui.PreferencesPageFactory;
import org.weasis.core.ui.editor.SeriesViewer;
import org.weasis.core.ui.editor.SeriesViewerFactory;
import org.weasis.core.ui.editor.image.ViewerPlugin;

/**
 * Application-wide SDK registry: prefs, factories, main window. Factories register at start; {@link
 * ViewerPlugin} instances are created on demand.
 */
public class UICore {

  private static final UICore INSTANCE = new UICore();
  private static final AtomicBoolean DOCKING_KEYS = new AtomicBoolean();

  private final WProperties systemPreferences = new WProperties();
  private final WProperties localPersistence = new WProperties();
  private final List<SeriesViewerFactory> viewerFactories = new CopyOnWriteArrayList<>();
  private final List<InsertableFactory> insertableFactories = new CopyOnWriteArrayList<>();
  private final List<PreferencesPageFactory> preferencesPageFactories =
      new CopyOnWriteArrayList<>();
  private final List<DataExplorerViewFactory> explorerFactories = new CopyOnWriteArrayList<>();
  private final List<DicomImportFactory> dicomImportFactories = new CopyOnWriteArrayList<>();
  private final List<ViewerPlugin<?>> openPlugins = new CopyOnWriteArrayList<>();
  private volatile JFrame applicationWindow;
  private volatile BundleContext bundleContext;
  private int selectedPluginIndex = -1;
  private boolean dockingListVisible;

  public static UICore getInstance() {
    return INSTANCE;
  }

  public WProperties getSystemPreferences() {
    return systemPreferences;
  }

  public WProperties getLocalPersistence() {
    return localPersistence;
  }

  public JFrame getApplicationWindow() {
    return applicationWindow;
  }

  public void setApplicationWindow(JFrame applicationWindow) {
    this.applicationWindow = applicationWindow;
  }

  public BundleContext getBundleContext() {
    return bundleContext;
  }

  public void setBundleContext(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }

  public void registerSeriesViewerFactory(SeriesViewerFactory factory) {
    if (factory != null && !viewerFactories.contains(factory)) {
      viewerFactories.add(factory);
    }
  }

  public void unregisterSeriesViewerFactory(SeriesViewerFactory factory) {
    viewerFactories.remove(factory);
  }

  public List<SeriesViewerFactory> getSeriesViewerFactories() {
    return Collections.unmodifiableList(viewerFactories);
  }

  public Optional<SeriesViewerFactory> getViewerFactory(String mimeType) {
    if (mimeType == null) {
      return Optional.empty();
    }
    return viewerFactories.stream()
        .filter(f -> f.canReadMimeType(mimeType))
        .sorted((a, b) -> Integer.compare(a.getLevel(), b.getLevel()))
        .findFirst();
  }

  public boolean isViewerCreatedByThisFactory(SeriesViewerFactory factory, SeriesViewer<?> viewer) {
    return factory != null && factory.isViewerCreatedByThisFactory(viewer);
  }

  public void registerInsertableFactory(InsertableFactory factory) {
    if (factory == null) {
      return;
    }
    if (factory instanceof PreferencesPageFactory pref) {
      registerPreferencesPageFactory(pref);
      return;
    }
    if (factory instanceof DataExplorerViewFactory explorer) {
      registerExplorerFactory(explorer);
      return;
    }
    if (!insertableFactories.contains(factory)) {
      insertableFactories.add(factory);
    }
  }

  public void unregisterInsertableFactory(InsertableFactory factory) {
    insertableFactories.remove(factory);
    if (factory instanceof PreferencesPageFactory pref) {
      preferencesPageFactories.remove(pref);
    }
    if (factory instanceof DataExplorerViewFactory explorer) {
      explorerFactories.remove(explorer);
    }
  }

  public List<InsertableFactory> getInsertableFactories() {
    return Collections.unmodifiableList(insertableFactories);
  }

  public void registerPreferencesPageFactory(PreferencesPageFactory factory) {
    if (factory != null && !preferencesPageFactories.contains(factory)) {
      preferencesPageFactories.add(factory);
    }
  }

  public List<PreferencesPageFactory> getPreferencesPageFactories() {
    return Collections.unmodifiableList(preferencesPageFactories);
  }

  public void registerExplorerFactory(DataExplorerViewFactory factory) {
    if (factory != null && !explorerFactories.contains(factory)) {
      explorerFactories.add(factory);
      if (!insertableFactories.contains(factory)) {
        insertableFactories.add(factory);
      }
    }
  }

  public List<DataExplorerViewFactory> getExplorerFactories() {
    return Collections.unmodifiableList(explorerFactories);
  }

  public void registerDicomImportFactory(DicomImportFactory factory) {
    if (factory != null && !dicomImportFactories.contains(factory)) {
      dicomImportFactories.add(factory);
    }
  }

  public void unregisterDicomImportFactory(DicomImportFactory factory) {
    dicomImportFactories.remove(factory);
  }

  public List<DicomImportFactory> getDicomImportFactories() {
    return Collections.unmodifiableList(dicomImportFactories);
  }

  public void openViewerPlugin(ViewerPlugin<?> plugin) {
    Objects.requireNonNull(plugin, "plugin");
    if (!openPlugins.contains(plugin)) {
      openPlugins.add(plugin);
    }
    setSelectedViewerPlugin(plugin);
  }

  public void closeViewerPlugin(ViewerPlugin<?> plugin) {
    if (plugin == null) {
      return;
    }
    int idx = openPlugins.indexOf(plugin);
    openPlugins.remove(plugin);
    plugin.close();
    if (idx >= 0 && idx < selectedPluginIndex) {
      selectedPluginIndex--;
    }
    if (openPlugins.isEmpty()) {
      selectedPluginIndex = -1;
      return;
    }
    if (selectedPluginIndex < 0 || selectedPluginIndex >= openPlugins.size()) {
      selectedPluginIndex = openPlugins.size() - 1;
    }
    setSelectedViewerPlugin(openPlugins.get(selectedPluginIndex));
  }

  public List<ViewerPlugin<?>> getOpenViewerPlugins() {
    return Collections.unmodifiableList(new ArrayList<>(openPlugins));
  }

  public ViewerPlugin<?> openBlankViewer(
      SeriesViewerFactory factory, Hashtable<String, Object> properties) {
    if (factory == null) {
      throw new IllegalArgumentException("factory");
    }
    SeriesViewer<?> created =
        factory.createSeriesViewer(properties == null ? new Hashtable<>() : properties);
    if (!(created instanceof ViewerPlugin<?> plugin)) {
      throw new IllegalStateException("Factory did not create a ViewerPlugin");
    }
    openViewerPlugin(plugin);
    return plugin;
  }

  public int getSelectedPluginIndex() {
    return selectedPluginIndex;
  }

  public ViewerPlugin<?> getSelectedViewerPlugin() {
    if (selectedPluginIndex < 0 || selectedPluginIndex >= openPlugins.size()) {
      return null;
    }
    return openPlugins.get(selectedPluginIndex);
  }

  public void setSelectedViewerPlugin(ViewerPlugin<?> plugin) {
    int idx = openPlugins.indexOf(plugin);
    if (idx < 0) {
      return;
    }
    selectedPluginIndex = idx;
    for (int i = 0; i < openPlugins.size(); i++) {
      openPlugins.get(i).setSelected(i == idx);
    }
  }

  public void cycleSelectedPlugin(boolean forward) {
    if (openPlugins.isEmpty()) {
      return;
    }
    int n = openPlugins.size();
    int next = forward ? selectedPluginIndex + 1 : selectedPluginIndex - 1;
    next = Math.floorMod(next, n);
    setSelectedViewerPlugin(openPlugins.get(next));
  }

  public void closeSelectedPlugin() {
    ViewerPlugin<?> selected = getSelectedViewerPlugin();
    if (selected != null) {
      closeViewerPlugin(selected);
    }
  }

  public void toggleMaximizeSelectedPlugin() {
    ViewerPlugin<?> selected = getSelectedViewerPlugin();
    if (selected != null) {
      selected.maximize();
    }
  }

  public void externalizeSelectedPlugin() {
    ViewerPlugin<?> selected = getSelectedViewerPlugin();
    if (selected != null) {
      selected.externalize();
    }
  }

  public void normalizeSelectedPlugin() {
    ViewerPlugin<?> selected = getSelectedViewerPlugin();
    if (selected != null) {
      selected.normalize();
    }
  }

  public void showDockingList() {
    dockingListVisible = true;
  }

  public boolean isDockingListVisible() {
    return dockingListVisible;
  }

  public List<String> dockingList() {
    List<String> names = new ArrayList<>();
    for (ViewerPlugin<?> plugin : openPlugins) {
      names.add(plugin.getPluginName());
    }
    return List.copyOf(names);
  }

  /**
   * Central-panel keys from SHORTCUTS.md: Ctrl+Tab cycle, Ctrl+M maximize/restore, Ctrl+W close,
   * Ctrl+E externalize, Ctrl+N normalize, Ctrl+Shift+E docking list.
   */
  public boolean handleDockingKey(KeyEvent e) {
    if (e == null) {
      return false;
    }
    int mods = e.getModifiersEx();
    if ((mods & InputEvent.CTRL_DOWN_MASK) == 0) {
      return false;
    }
    boolean shift = (mods & InputEvent.SHIFT_DOWN_MASK) != 0;
    int code = e.getKeyCode();
    if (code == KeyEvent.VK_TAB) {
      cycleSelectedPlugin(!shift);
      return true;
    }
    if (shift && code == KeyEvent.VK_E) {
      showDockingList();
      return true;
    }
    if (shift) {
      return false;
    }
    return switch (code) {
      case KeyEvent.VK_M -> {
        toggleMaximizeSelectedPlugin();
        yield true;
      }
      case KeyEvent.VK_W -> {
        closeSelectedPlugin();
        yield true;
      }
      case KeyEvent.VK_E -> {
        externalizeSelectedPlugin();
        yield true;
      }
      case KeyEvent.VK_N -> {
        normalizeSelectedPlugin();
        yield true;
      }
      default -> false;
    };
  }

  public void installDockingKeyDispatcher() {
    if (!DOCKING_KEYS.compareAndSet(false, true)) {
      return;
    }
    KeyboardFocusManager.getCurrentKeyboardFocusManager()
        .addKeyEventDispatcher(e -> e.getID() == KeyEvent.KEY_PRESSED && handleDockingKey(e));
  }
}
