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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import org.weasis.core.api.media.data.MediaElement;
import org.weasis.core.api.media.data.MediaSeries;
import org.weasis.core.ui.editor.image.ImageViewerPlugin;
import org.weasis.core.ui.editor.image.MeasureToolBar;
import org.weasis.core.ui.editor.image.RotationToolBar;
import org.weasis.core.ui.editor.image.ScreenshotToolBar;
import org.weasis.core.ui.editor.image.SynchView;
import org.weasis.core.ui.editor.image.ViewerToolBar;
import org.weasis.core.ui.editor.image.ZoomToolBar;
import org.weasis.core.ui.util.ToolBarContainer;

/** One tab: ImageViewerPlugin holding a {@link View2d}. MPR is {@code mpr.MprContainer}. */
public class View2dContainer extends ImageViewerPlugin<MediaElement> {

  public static final String NAME = "DICOM 2D";

  private final View2d view2d = new View2d();
  private final JPanel viewGrid = new JPanel(new GridLayout(1, 1));
  private final List<View2d> layout = new CopyOnWriteArrayList<>();
  private final DicomSynchManager synchManager = new DicomSynchManager();
  private final ToolBarContainer toolbars = new ToolBarContainer();
  private final ViewerToolBar viewerToolBar = new ViewerToolBar();
  private final LutToolBar lutToolBar = new LutToolBar();
  private final ZoomToolBar zoomToolBar = new ZoomToolBar();
  private final RotationToolBar rotationToolBar = new RotationToolBar();
  private final ResetTools resetTools = new ResetTools();
  private final DcmHeaderToolBar headerToolBar = new DcmHeaderToolBar();
  private final ScreenshotToolBar screenshotToolBar = new ScreenshotToolBar();
  private final CineToolBar cineToolBar = new CineToolBar();
  private final MeasureToolBar measureToolBar = new MeasureToolBar();
  private final KeyObjectToolBar keyObjectToolBar = new KeyObjectToolBar();
  private int layoutIndex;

  public View2dContainer() {
    super(NAME);
    layout.add(view2d);
    bindToolBars();
    add(toolbars, BorderLayout.NORTH);
    add(viewGrid, BorderLayout.CENTER);
    view2d.putClientProperty(View2dContainer.class, this);
    view2d.setSynchManager(synchManager);
    synchManager.add(view2d);
    View2dRegistry.register(view2d);
    View2dRegistry.select(view2d);
    relayoutViews();
  }

  void bindToolBars() {
    toolbars.registerToolBar(viewerToolBar);
    toolbars.registerToolBar(lutToolBar);
    toolbars.registerToolBar(zoomToolBar);
    toolbars.registerToolBar(rotationToolBar);
    toolbars.registerToolBar(resetTools);
    toolbars.registerToolBar(headerToolBar);
    toolbars.registerToolBar(screenshotToolBar);
    toolbars.registerToolBar(cineToolBar);
    toolbars.registerToolBar(measureToolBar);
    toolbars.registerToolBar(keyObjectToolBar);
    viewerToolBar.bind(view2d);
    lutToolBar.bind(view2d);
    resetTools.bind(view2d);
    headerToolBar.bind(view2d);
    cineToolBar.bind(view2d);
    measureToolBar.bind(view2d);
    keyObjectToolBar.bind(view2d);
  }

  public ToolBarContainer getToolBars() {
    return toolbars;
  }

  public ViewerToolBar getViewerToolBar() {
    return viewerToolBar;
  }

  public LutToolBar getLutToolBar() {
    return lutToolBar;
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

  JPanel getViewGrid() {
    return viewGrid;
  }

  @Override
  public void setLayoutCount(int n) {
    int count = Math.max(1, n);
    growLayout(count);
    shrinkLayout(count);
    layoutIndex = Math.min(layoutIndex, layout.size() - 1);
    relayoutViews();
  }

  void growLayout(int count) {
    while (layout.size() < count) {
      layout.add(newView2d());
    }
  }

  void shrinkLayout(int count) {
    while (layout.size() > count) {
      View2d removed = layout.remove(layout.size() - 1);
      synchManager.remove(removed);
      View2dRegistry.unregister(removed);
    }
  }

  View2d newView2d() {
    View2d extra = new View2d();
    extra.setSynchManager(synchManager);
    synchManager.add(extra);
    extra.putClientProperty(View2dContainer.class, this);
    View2dRegistry.register(extra);
    copyPrimaryInto(extra, view2d.getSeries());
    return extra;
  }

  void copyPrimaryInto(View2d extra, MediaSeries<? extends MediaElement> sequence) {
    if (sequence != null) {
      extra.setSeries(sequence);
    }
    if (view2d.getDataset() != null) {
      extra.load(view2d.getDataset());
    }
  }

  void relayoutViews() {
    viewGrid.removeAll();
    viewGrid.setLayout(gridForCount(layout.size()));
    for (View2d v : layout) {
      viewGrid.add(v);
    }
    viewGrid.revalidate();
    viewGrid.repaint();
  }

  static GridLayout gridForCount(int n) {
    int cols = Math.min(2, Math.max(1, n));
    int rows = (n + cols - 1) / cols;
    return new GridLayout(rows, cols);
  }

  @Override
  public int getLayoutCount() {
    return layout.size();
  }

  @Override
  public void resetDisplay() {
    resetTools.apply(org.weasis.core.ui.editor.image.ResetTools.ALL);
  }

  @Override
  public void applyPreset(int index) {
    focusedLayoutView().applyPreset(index);
  }

  View2d focusedLayoutView() {
    if (layoutIndex >= 0 && layoutIndex < layout.size()) {
      return layout.get(layoutIndex);
    }
    return view2d;
  }

  @Override
  public void selectAllGraphics() {
    focusedLayoutView().selectAllGraphics();
  }

  @Override
  public void deselectAllGraphics() {
    focusedLayoutView().deselectAllGraphics();
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

  public void cycleLayout(int delta) {
    if (layout.size() <= 1) {
      return;
    }
    setLayoutIndex(Math.floorMod(layoutIndex + delta, layout.size()));
  }

  @Override
  public synchronized void addSeries(MediaSeries<MediaElement> sequence) {
    super.addSeries(sequence);
    if (sequence == null || sequence.getMedias().isEmpty()) {
      return;
    }
    placeSeries(sequence);
  }

  @Override
  public void hangSeries(List<MediaSeries<MediaElement>> series) {
    if (series == null || series.isEmpty()) {
      return;
    }
    growToHung(series.size());
    hangPrimary(series.getFirst());
    hangRest(series);
  }

  void growToHung(int n) {
    if (layout.size() < n) {
      setLayoutCount(n);
    }
  }

  void hangPrimary(MediaSeries<MediaElement> primary) {
    super.addSeries(primary);
    view2d.setSeries(primary);
    loadFirstMedia(primary);
  }

  void hangRest(List<MediaSeries<MediaElement>> series) {
    MediaSeries<MediaElement> primary = series.getFirst();
    for (int i = 1; i < layout.size(); i++) {
      MediaSeries<MediaElement> cell = seriesAt(series, i, primary);
      hangCell(layout.get(i), cell);
      rememberHung(cell, primary);
    }
  }

  static MediaSeries<MediaElement> seriesAt(
      List<MediaSeries<MediaElement>> series, int i, MediaSeries<MediaElement> primary) {
    return i < series.size() ? series.get(i) : primary;
  }

  void rememberHung(MediaSeries<MediaElement> cell, MediaSeries<MediaElement> primary) {
    if (cell != null && cell != primary) {
      super.addSeries(cell);
    }
  }

  void placeSeries(MediaSeries<MediaElement> sequence) {
    int slot = nextHangSlot();
    if (slot == 0) {
      putPrimary(sequence);
      return;
    }
    hangCell(layout.get(slot), sequence);
  }

  void putPrimary(MediaSeries<MediaElement> sequence) {
    view2d.setSeries(sequence);
    loadFirstMedia(sequence);
    fillOtherLayoutViews(sequence);
  }

  int nextHangSlot() {
    if (view2d.getSeries() == null) {
      return 0;
    }
    return firstCloneSlot();
  }

  int firstCloneSlot() {
    MediaSeries<?> primary = view2d.getSeries();
    for (int i = 1; i < layout.size(); i++) {
      if (isCloneSlot(layout.get(i), primary)) {
        return i;
      }
    }
    return 0;
  }

  static boolean isCloneSlot(View2d cell, MediaSeries<?> primary) {
    MediaSeries<?> hung = cell.getSeries();
    return hung == null || hung == primary;
  }

  void hangCell(View2d cell, MediaSeries<MediaElement> sequence) {
    if (sequence == null) {
      return;
    }
    cell.setSeries(sequence);
    loadInto(cell, sequence);
  }

  void loadFirstMedia(MediaSeries<MediaElement> sequence) {
    loadInto(view2d, sequence);
    view2d.setSynch(SynchView.STACK);
  }

  void loadInto(View2d cell, MediaSeries<? extends MediaElement> sequence) {
    if (sequence == null || sequence.getMedias().isEmpty()) {
      return;
    }
    URI uri = sequence.getMedias().getFirst().getMediaURI();
    if (uri == null) {
      return;
    }
    tryLoad(cell, uri);
  }

  void tryLoad(View2d cell, URI uri) {
    try {
      cell.load(new File(uri));
    } catch (Exception e) {
      cell.setGeometryWarning("Unable to open DICOM");
    }
  }

  void fillOtherLayoutViews(MediaSeries<MediaElement> sequence) {
    MediaSeries<?> primary = view2d.getSeries();
    for (View2d v : layout) {
      if (v != view2d && isCloneSlot(v, primary)) {
        copyPrimaryInto(v, sequence);
      }
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
