/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.dialog;

import java.util.List;
import java.util.Properties;
import org.weasis.acquire.explorer.AcquireDest;
import org.weasis.acquire.explorer.PublishDicomTask;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;

/**
 * Publish options: all vs selection, optional resolution downscale, destination from {@code
 * weasis.acquire.dest.*}.
 */
public class AcquirePublishDialog {

  public enum Scope {
    ALL,
    SELECTION
  }

  private Scope scope = Scope.ALL;
  private int resolutionDownscale;
  private Properties preferences = new Properties();

  public Scope scope() {
    return scope;
  }

  public void setScope(Scope scope) {
    this.scope = scope == null ? Scope.ALL : scope;
  }

  public int resolutionDownscale() {
    return resolutionDownscale;
  }

  public void setResolutionDownscale(int resolutionDownscale) {
    this.resolutionDownscale = Math.max(0, resolutionDownscale);
  }

  public void setOriginalResolution() {
    this.resolutionDownscale = 0;
  }

  public Properties preferences() {
    return preferences;
  }

  public void setPreferences(Properties preferences) {
    this.preferences = preferences == null ? new Properties() : preferences;
  }

  public boolean selectionOnly() {
    return scope == Scope.SELECTION;
  }

  public List<Item> imagesForPublish(List<Item> all, List<Item> selected) {
    if (scope == Scope.SELECTION) {
      return selected == null ? List.of() : List.copyOf(selected);
    }
    return all == null ? List.of() : List.copyOf(all);
  }

  public AcquireDest.Publication plan(String callingAe) {
    return new PublishDicomTask(preferences, selectionOnly(), resolutionDownscale).plan(callingAe);
  }
}
