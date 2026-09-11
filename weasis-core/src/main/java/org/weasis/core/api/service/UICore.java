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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
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
  }

  public void closeViewerPlugin(ViewerPlugin<?> plugin) {
    if (plugin != null) {
      openPlugins.remove(plugin);
      plugin.close();
    }
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
}
