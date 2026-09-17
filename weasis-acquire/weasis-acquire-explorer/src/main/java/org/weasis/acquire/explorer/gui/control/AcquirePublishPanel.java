/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.gui.control;

import java.awt.BorderLayout;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.AcquireDest;
import org.weasis.acquire.explorer.gui.central.tumbnail.AcquireCentralThumbnailModel.Item;
import org.weasis.acquire.explorer.gui.dialog.AcquirePublishDialog;
import org.weasis.acquire.explorer.gui.model.publish.PublishTree;

/**
 * Publish chrome: dialog scope/resolution plus checkbox tree of stills, then {@code
 * PublishDicomTask} plan.
 */
public class AcquirePublishPanel extends JPanel {

  private final AcquirePublishDialog dialog;
  private final PublishTree tree;

  public AcquirePublishPanel() {
    this(new AcquirePublishDialog(), new PublishTree());
  }

  public AcquirePublishPanel(AcquirePublishDialog dialog, PublishTree tree) {
    super(new BorderLayout());
    this.dialog = dialog == null ? new AcquirePublishDialog() : dialog;
    this.tree = tree == null ? new PublishTree() : tree;
    add(new JLabel("Publish"), BorderLayout.NORTH);
  }

  public AcquirePublishDialog dialog() {
    return dialog;
  }

  public PublishTree tree() {
    return tree;
  }

  public AcquireDest.Publication prepare(
      Properties prefs, List<Item> all, List<Item> selected, String callingAe) {
    dialog.setPreferences(prefs);
    tree.load(dialog.imagesForPublish(all, selected));
    return dialog.plan(callingAe);
  }

  public List<Path> checkedFiles() {
    return tree.checkedFiles();
  }
}
