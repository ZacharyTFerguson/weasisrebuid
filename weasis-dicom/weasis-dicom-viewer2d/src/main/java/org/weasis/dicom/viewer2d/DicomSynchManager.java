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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.ui.editor.image.DefaultView2d;
import org.weasis.core.ui.editor.image.SynchData;
import org.weasis.core.ui.editor.image.SynchManager;
import org.weasis.core.ui.editor.image.SynchView;

/**
 * MX-14: Frame of Reference synch groups by FoR UID; manual synch ignores FoR and follows the
 * explicit group. Those kinds are distinct.
 */
public class DicomSynchManager implements SynchManager {

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
    int frame = source.getFrameIndex();
    for (DefaultView2d<?> view : views) {
      if (follows(source, view)) {
        view.setFrameIndex(frame, false);
      }
    }
  }

  @Override
  public void onCrosshair(DefaultView2d<?> source) {
    if (source == null || source.getSynch() == SynchView.NONE) {
      return;
    }
    for (DefaultView2d<?> view : views) {
      if (follows(source, view)) {
        view.setCrosshair(source.getCrosshairX(), source.getCrosshairY(), false);
      }
    }
  }

  boolean follows(DefaultView2d<?> source, DefaultView2d<?> view) {
    if (view == source || view.getSynch() == SynchView.NONE) {
      return false;
    }
    SynchData.Kind kind = source.getSynchData().getKind();
    if (kind == SynchData.Kind.FRAME_OF_REFERENCE) {
      return view.getSynchData().getKind() == SynchData.Kind.FRAME_OF_REFERENCE
          && Objects.equals(source.getFrameOfReferenceUID(), view.getFrameOfReferenceUID())
          && !source.getFrameOfReferenceUID().isBlank();
    }
    return kind == SynchData.Kind.MANUAL && view.getSynchData().getKind() == SynchData.Kind.MANUAL;
  }

  public List<DefaultView2d<?>> getViews() {
    return List.copyOf(views);
  }
}
