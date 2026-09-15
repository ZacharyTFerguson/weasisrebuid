/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor.image;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Manual-group synch: only views marked {@link SynchData.Kind#MANUAL} follow each other. */
public class DefaultSynchManager implements SynchManager {

  private final List<DefaultView2d<?>> views = new CopyOnWriteArrayList<>();

  @Override
  public void add(DefaultView2d<?> view) {
    if (view != null) {
      views.add(view);
    }
  }

  @Override
  public void remove(DefaultView2d<?> view) {
    views.remove(view);
  }

  @Override
  public void onFrame(DefaultView2d<?> source) {
    if (source == null || source.getSynch() == SynchView.NONE) {
      return;
    }
    if (source.getSynchData().getKind() != SynchData.Kind.MANUAL) {
      return;
    }
    int frame = source.getFrameIndex();
    for (DefaultView2d<?> view : views) {
      if (view == source || view.getSynch() == SynchView.NONE) {
        continue;
      }
      if (view.getSynchData().getKind() == SynchData.Kind.MANUAL) {
        view.setFrameIndex(frame, false);
      }
    }
  }

  public List<DefaultView2d<?>> getViews() {
    return List.copyOf(views);
  }
}
