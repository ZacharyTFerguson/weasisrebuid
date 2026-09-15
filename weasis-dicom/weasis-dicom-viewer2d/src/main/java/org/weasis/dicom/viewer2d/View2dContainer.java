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

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.SynchView;

/** One tab: ImageViewerPlugin holding a {@link View2d}. MPR is {@code mpr.MprContainer}. */
public class View2dContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM 2D";

  private final View2d view2d = new View2d();
  private final List<View2d> layout = new CopyOnWriteArrayList<>();
  private final DicomSynchManager synchManager = new DicomSynchManager();
  private int layoutIndex;

  public View2dContainer() {
    super(NAME);
    layout.add(view2d);
    add(view2d);
    view2d.putClientProperty(View2dContainer.class, this);
    view2d.setSynchManager(synchManager);
    synchManager.add(view2d);
    View2dRegistry.register(view2d);
    View2dRegistry.select(view2d);
  }

  public View2d getView2d() {
    return view2d;
  }

  public DicomSynchManager getSynchManager() {
    return synchManager;
  }

  public List<View2d> getLayoutViews() {
    return List.copyOf(layout);
  }

  public void setLayoutCount(int n) {
    int count = Math.max(1, n);
    while (layout.size() < count) {
      View2d extra = new View2d();
      extra.setSynchManager(synchManager);
      synchManager.add(extra);
      layout.add(extra);
      View2dRegistry.register(extra);
    }
    while (layout.size() > count) {
      View2d removed = layout.remove(layout.size() - 1);
      synchManager.remove(removed);
      View2dRegistry.unregister(removed);
    }
    layoutIndex = Math.min(layoutIndex, layout.size() - 1);
  }

  public int getLayoutIndex() {
    return layoutIndex;
  }

  public void setLayoutIndex(int index) {
    if (index >= 0 && index < layout.size()) {
      layoutIndex = index;
      View2dRegistry.select(layout.get(index));
    }
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null) {
      return;
    }
    List<MediaElement> medias = sequence.getMedias();
    if (medias.isEmpty()) {
      return;
    }
    URI uri = medias.getFirst().getMediaURI();
    if (uri == null) {
      return;
    }
    try {
      view2d.load(new File(uri));
      view2d.setSynch(SynchView.STACK);
    } catch (Exception e) {
      view2d.setGeometryWarning("Unable to open DICOM");
    }
  }

  @Override
  public void close() {
    View2dRegistry.unregister(view2d);
    for (View2d v : layout) {
      View2dRegistry.unregister(v);
    }
    super.close();
  }
}
