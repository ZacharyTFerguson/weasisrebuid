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

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import org.weasis.core.api.gui.util.ActionW;
import org.weasis.core.ui.editor.image.dockable.MeasureTool;
import org.weasis.core.ui.util.WtoolBar;

/**
 * 2D viewer mouse-action chrome. Buttons are the Gogo {@code dcmview2d:mouseLeftAction} tokens from
 * COMMANDS.md.
 */
public class ViewerToolBar extends WtoolBar {

  public static final String NAME = "Viewer";

  public static final String[] ACTIONS = {
    ActionW.SCROLL_SERIES.cmd(),
    ActionW.WINLEVEL.cmd(),
    ActionW.ZOOM.cmd(),
    ActionW.PAN.cmd(),
    ActionW.ROTATION.cmd(),
    ActionW.CROSSHAIR.cmd(),
    ActionW.MEASURE.cmd(),
    ActionW.DRAW.cmd(),
    ActionW.CONTEXTMENU.cmd(),
    ActionW.NONE.cmd()
  };

  private String selected = ActionW.WINLEVEL.cmd();
  private DefaultView2d<?> view;
  private DefaultView2d<?> statsView;
  private final JLabel pixelInfo = new JLabel(" ");
  private final JLabel regionStats = new JLabel(" ");
  private final GraphicsPane graphicsPane = new GraphicsPane();
  private final SynchViewButton synchFor = new SynchViewButton();
  private final ManualSynchViewButton synchManual = new ManualSynchViewButton();
  private final JLabel synchKind = new JLabel("FoR");
  private final JButton leftT = new JButton("pan");
  private final JButton leftW = new JButton("winLevel");
  private final JLabel leftState = new JLabel("none");

  public ViewerToolBar() {
    super(NAME, 10);
    for (String action : ACTIONS) {
      add(button(action));
    }
    pixelInfo.setName("pixel-info");
    add(pixelInfo);
    regionStats.setName("region-stats");
    add(regionStats);
    add(graphicsPane);
    nameSynchChrome();
    add(synchFor);
    add(synchManual);
    add(synchKind);
    addLeftKeyChrome();
  }

  void nameSynchChrome() {
    synchKind.setName("synch-kind");
    ButtonGroup group = new ButtonGroup();
    group.add(synchFor);
    group.add(synchManual);
    synchFor.addActionListener(e -> applyFor());
    synchManual.addActionListener(e -> applyManual());
  }

  public void bind(DefaultView2d<?> view) {
    this.view = view;
    if (view != null) {
      apply(view);
      view.setCrosshairListener((v, info) -> refreshPixelInfo(info));
      refreshPixelInfo(view.getPixelInfo());
      refreshSynchKind();
      wireRegionStats(view);
      refreshRegionStats();
      graphicsPane.bind(view);
    }
  }

  void wireRegionStats(DefaultView2d<?> view) {
    if (view == statsView) {
      return;
    }
    statsView = view;
    view.addGraphicSelectionListener(selected -> refreshRegionStats());
    view.addGraphicModelChangeListener(this::refreshRegionStats);
  }

  public void refreshRegionStats() {
    if (view == null) {
      regionStats.setText(" ");
      return;
    }
    regionStats.setText(ImageRegionStatistics.compute(view).text());
  }

  public String getSelected() {
    return selected;
  }

  public void setSelected(String selected) {
    this.selected = selected == null || selected.isBlank() ? ActionW.WINLEVEL.cmd() : selected;
  }

  public void apply(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    view.getMouseActions().setLeft(selected);
    bindMeasureTool(view);
  }

  public static void bindMeasureTool(DefaultView2d<?> view) {
    if (view == null) {
      return;
    }
    String left = MouseActions.normalize(view.getMouseActions().getLeft());
    if (!drawingLeft(left)) {
      return;
    }
    MeasureToolBar bar = view.getMeasureToolBar();
    if (bar != null) {
      bindBar(view, bar, left);
      return;
    }
    bindWithoutBar(view, left);
  }

  static boolean drawingLeft(String left) {
    return MouseActions.MEASURE.equals(left) || MouseActions.DRAW.equals(left);
  }

  static void bindBar(DefaultView2d<?> view, MeasureToolBar bar, String left) {
    if (MouseActions.DRAW.equals(left)) {
      bindDrawBar(bar);
      return;
    }
    keepBarTool(view, bar);
  }

  static void bindDrawBar(MeasureToolBar bar) {
    if (!MeasureTool.drawFamily(bar.getSelected())) {
      bar.setSelected(MeasureTool.RECTANGLE);
    }
    bar.applyAll();
  }

  static void keepBarTool(DefaultView2d<?> view, MeasureToolBar bar) {
    view.setMeasureTool(MeasureTool.canonical(bar.getSelected()));
  }

  static void syncLeftToTool(DefaultView2d<?> view) {
    if (MeasureTool.drawFamily(view.activeMeasureTool())) {
      view.getMouseActions().setLeft(MouseActions.DRAW);
    } else {
      view.getMouseActions().setLeft(MouseActions.MEASURE);
    }
  }

  static void bindWithoutBar(DefaultView2d<?> view, String left) {
    if (MouseActions.DRAW.equals(left)) {
      bindDrawWithoutBar(view);
      return;
    }
    keepWithoutBar(view);
  }

  static void bindDrawWithoutBar(DefaultView2d<?> view) {
    if (!MeasureTool.drawFamily(view.getMeasureTool())) {
      view.setMeasureTool(MeasureTool.RECTANGLE);
    }
  }

  static void keepWithoutBar(DefaultView2d<?> view) {
    String tool = view.getMeasureTool();
    if (MeasureTool.measureFamily(tool) || MeasureTool.drawFamily(tool)) {
      return;
    }
    view.setMeasureTool(MeasureTool.DISTANCE);
  }

  public DefaultView2d<?> boundView() {
    return view;
  }

  public JLabel pixelInfoLabel() {
    return pixelInfo;
  }

  public String pixelInfoText() {
    return pixelInfo.getText();
  }

  public JLabel regionStatsLabel() {
    return regionStats;
  }

  public String regionStatsText() {
    return regionStats.getText();
  }

  public GraphicsPane graphicsPane() {
    return graphicsPane;
  }

  public void refreshPixelInfo(PixelInfo info) {
    if (info != null && !info.getText().isBlank()) {
      pixelInfo.setText(info.getText());
      return;
    }
    pixelInfo.setText(" ");
  }

  public SynchViewButton synchForButton() {
    return synchFor;
  }

  public ManualSynchViewButton synchManualButton() {
    return synchManual;
  }

  public JLabel synchKindLabel() {
    return synchKind;
  }

  public String synchKindText() {
    return synchKind.getText();
  }

  void applyFor() {
    if (view != null) {
      synchFor.apply(view);
    }
    refreshSynchKind();
  }

  void applyManual() {
    if (view != null) {
      synchManual.apply(view);
    }
    refreshSynchKind();
  }

  void refreshSynchKind() {
    if (view != null && view.getSynchData().isManual()) {
      synchManual.setSelected(true);
      synchKind.setText("Manual");
      return;
    }
    synchFor.setSelected(true);
    synchKind.setText("FoR");
  }

  void addLeftKeyChrome() {
    leftT.setName("left-t");
    leftW.setName("left-w");
    leftState.setName("left-state");
    leftT.addActionListener(e -> applyLeftT());
    leftW.addActionListener(e -> applyLeftW());
    add(leftT);
    add(leftW);
    add(leftState);
  }

  void applyLeftT() {
    setSelected(ActionW.PAN.cmd());
    apply(view);
    leftState.setText("pan");
  }

  void applyLeftW() {
    setSelected(ActionW.WINLEVEL.cmd());
    apply(view);
    leftState.setText("winLevel");
  }

  public JButton leftTButton() {
    return leftT;
  }

  public JButton leftWButton() {
    return leftW;
  }

  public JLabel leftStateLabel() {
    return leftState;
  }

  public String leftStateText() {
    return leftState.getText();
  }

  private JButton button(String action) {
    JButton button =
        new JButton(
            new AbstractAction(action) {
              @Override
              public void actionPerformed(ActionEvent e) {
                setSelected(action);
                apply(view);
              }
            });
    button.setToolTipText(action);
    button.setName(action);
    return button;
  }
}
