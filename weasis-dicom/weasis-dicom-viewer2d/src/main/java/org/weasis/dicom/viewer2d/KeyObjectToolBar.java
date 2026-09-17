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

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.weasis.core.ui.model.graphic.Graphic;
import org.weasis.core.ui.util.Toolbar;
import org.weasis.core.ui.util.WtoolBar;

/**
 * Key Object chrome. Star toggles the current SOP as a key image (shortcut K); Filter shows only
 * key images; Save KO/PR emit objects with root UID 2.25 (WP-5).
 */
public class KeyObjectToolBar extends WtoolBar implements Toolbar {

  public static final String NAME = "Key Object";
  public static final String STAR = "star";
  public static final String FILTER = "ko-filter";
  public static final String SAVE_KO = "save-ko";
  public static final String SAVE_PR = "save-pr";
  public static final String KO_UID = "ko-uid";
  public static final String PR_UID = "pr-uid";

  private KOManager manager;
  private View2d view;
  private final PRManager prManager = new PRManager();
  private final JButton star = actionButton(STAR, "Star");
  private final JToggleButton filter = new JToggleButton("Filter");
  private final JLabel state = new JLabel("none");
  private final JButton saveKo = actionButton(SAVE_KO, "Save KO");
  private final JLabel koUid = new JLabel("none");
  private final JButton savePr = actionButton(SAVE_PR, "Save PR");
  private final JLabel prUid = new JLabel("none");

  public KeyObjectToolBar() {
    this(new KOManager());
  }

  public KeyObjectToolBar(KOManager manager) {
    super(NAME, 12);
    this.manager = manager == null ? new KOManager() : manager;
    setName("key-object");
    add(star);
    filter.setName(FILTER);
    filter.setToolTipText("Filter");
    filter.addActionListener(e -> filter());
    add(filter);
    state.setName("ko-state");
    add(state);
    add(saveKo);
    koUid.setName(KO_UID);
    add(koUid);
    add(savePr);
    prUid.setName(PR_UID);
    add(prUid);
  }

  public void bind(View2d view) {
    this.view = view;
    if (view != null) {
      this.manager = view.getKoManager();
    }
    syncFilter();
    refreshState();
  }

  public View2d boundView() {
    return view;
  }

  public KOManager getManager() {
    return view != null ? view.getKoManager() : manager;
  }

  public boolean toggle(String sopInstanceUid) {
    return getManager().toggleKeyImage(sopInstanceUid);
  }

  public boolean star() {
    boolean on = view != null && view.toggleKeyImage();
    refreshState();
    return on;
  }

  public boolean filter() {
    KOManager ko = getManager();
    ko.setFilterKeyImages(!ko.isFilterKeyImages());
    if (view != null) {
      view.applyKeyImageFilter();
    }
    syncFilter();
    refreshState();
    return ko.isFilterKeyImages();
  }

  public boolean isFilterKeyImages() {
    return getManager().isFilterKeyImages();
  }

  public JButton starButton() {
    return star;
  }

  public JToggleButton filterButton() {
    return filter;
  }

  public JLabel stateLabel() {
    return state;
  }

  public String stateText() {
    return state.getText();
  }

  public JButton saveKoButton() {
    return saveKo;
  }

  public JButton savePrButton() {
    return savePr;
  }

  public JLabel koUidLabel() {
    return koUid;
  }

  public JLabel prUidLabel() {
    return prUid;
  }

  public String koUidText() {
    return koUid.getText();
  }

  public String prUidText() {
    return prUid.getText();
  }

  public String saveKo() {
    String uid = sopOf(getManager().buildKoDocument(source()));
    koUid.setText(uid);
    return uid;
  }

  public String savePr() {
    String uid = sopOf(prManager.buildPresentationState(source(), drawings()));
    prUid.setText(uid);
    return uid;
  }

  Attributes source() {
    return view == null ? null : view.getDataset();
  }

  List<Graphic> drawings() {
    return view == null ? List.of() : view.getSelectedGraphics();
  }

  static String sopOf(Attributes dcm) {
    if (dcm == null) {
      return "none";
    }
    String uid = dcm.getString(Tag.SOPInstanceUID);
    return uid == null || uid.isBlank() ? "none" : uid;
  }

  public void syncFilter() {
    filter.setSelected(isFilterKeyImages());
  }

  void refreshState() {
    if (isFilterKeyImages()) {
      state.setText("filtered");
      return;
    }
    if (currentIsStarred()) {
      state.setText("starred");
      return;
    }
    state.setText("none");
  }

  boolean currentIsStarred() {
    if (view == null || view.getDataset() == null) {
      return false;
    }
    return getManager().isKeyImage(view.getDataset().getString(Tag.SOPInstanceUID));
  }

  JButton actionButton(String name, String label) {
    JButton button =
        new JButton(
            new AbstractAction(label) {
              @Override
              public void actionPerformed(ActionEvent e) {
                clickNamed(name);
              }
            });
    button.setName(name);
    button.setToolTipText(label);
    return button;
  }

  void clickNamed(String name) {
    if (STAR.equals(name)) {
      star();
      return;
    }
    if (SAVE_KO.equals(name)) {
      saveKo();
      return;
    }
    if (SAVE_PR.equals(name)) {
      savePr();
    }
  }

  @Override
  public JComponent getComponent() {
    return this;
  }
}
