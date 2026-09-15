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
import java.awt.FlowLayout;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.weasis.acquire.explorer.core.ItemList;
import org.weasis.acquire.explorer.gui.list.AcquireThumbnailListPane;
import org.weasis.acquire.explorer.gui.model.actions.ChangePathSelectionAction;
import org.weasis.acquire.explorer.gui.model.list.ItemListComboBoxModel;
import org.weasis.acquire.explorer.gui.model.renderer.MediaSourceListCellRenderer;
import org.weasis.acquire.explorer.media.FileSystemDrive;
import org.weasis.acquire.explorer.media.MediaSource;

/** Selects a {@link MediaSource} and lists stills from a {@link FileSystemDrive}. */
public class BrowsePanel extends JPanel {

  private final ItemList<MediaSource> sources = new ItemList<>();
  private final ItemListComboBoxModel<MediaSource> comboModel =
      new ItemListComboBoxModel<>(sources);
  private final JComboBox<MediaSource> combo = new JComboBox<>(comboModel);
  private final ChangePathSelectionAction changePath = new ChangePathSelectionAction(this);
  private final AcquireThumbnailListPane thumbnails = new AcquireThumbnailListPane();
  private boolean selecting;

  public BrowsePanel() {
    super(new BorderLayout());
    combo.setRenderer(new MediaSourceListCellRenderer());
    combo.setName("media-source");
    combo.addActionListener(
        e -> {
          if (selecting) {
            return;
          }
          Object selected = combo.getSelectedItem();
          if (selected instanceof MediaSource source) {
            select(source);
          }
        });
    JButton change = new JButton(changePath);
    change.setName("change-path");
    JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
    north.add(combo);
    north.add(change);
    add(north, BorderLayout.NORTH);
    add(thumbnails, BorderLayout.CENTER);
  }

  public ItemList<MediaSource> sources() {
    return sources;
  }

  public ItemListComboBoxModel<MediaSource> comboModel() {
    return comboModel;
  }

  public JComboBox<MediaSource> combo() {
    return combo;
  }

  public ChangePathSelectionAction changePath() {
    return changePath;
  }

  public AcquireThumbnailListPane thumbnails() {
    return thumbnails;
  }

  public MediaSourceListCellRenderer renderer() {
    return (MediaSourceListCellRenderer) combo.getRenderer();
  }

  public void addSource(MediaSource source) {
    if (source == null) {
      return;
    }
    sources.addItem(source);
    if (sources.size() == 1) {
      select(source);
    }
  }

  public MediaSource selected() {
    return sources.getCurrentItem();
  }

  public void select(MediaSource source) {
    if (source == null || selecting) {
      return;
    }
    selecting = true;
    try {
      sources.setCurrentItem(source);
      if (!Objects.equals(combo.getSelectedItem(), source)) {
        combo.setSelectedItem(source);
      }
      reloadStills();
    } finally {
      selecting = false;
    }
  }

  public void reloadStills() {
    if (selected() instanceof FileSystemDrive drive) {
      thumbnails.setItems(drive.listStills());
    } else {
      thumbnails.setItems(List.of());
    }
  }
}
