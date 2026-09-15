/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.explorer.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.weasis.core.api.explorer.ObservableEvent;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaSeriesGroup;
import org.weasis.core.api.media.data.MediaSeriesGroupNode;
import org.weasis.core.api.media.data.TagW;

public abstract class AbstractFileModel implements DataExplorerModel, TreeModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final MediaSeriesGroupNode root = new MediaSeriesGroupNode(TagW.PatientID, "root");
  private final List<Codec> codecs = new ArrayList<>();

  public MediaSeriesGroup getRoot() {
    return root;
  }

  public void addCodec(Codec codec) {
    if (codec != null) {
      codecs.add(codec);
    }
  }

  @Override
  public Codec[] getCodecPlugins() {
    return codecs.toArray(Codec[]::new);
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    pcs.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    pcs.removePropertyChangeListener(listener);
  }

  @Override
  public void firePropertyChange(ObservableEvent event) {
    if (event != null) {
      pcs.firePropertyChange(String.valueOf(event.getActionCommand()), null, event.getNewValue());
    }
  }

  @Override
  public ListNodes getModelStructure() {
    return () ->
        new TreeModelNode[] {
          TreeModelNode.PATIENT, TreeModelNode.STUDY, TreeModelNode.SERIES, TreeModelNode.IMAGE
        };
  }

  @Override
  public Collection<MediaSeriesGroup> getChildren(MediaSeriesGroup node) {
    return List.of();
  }

  @Override
  public MediaSeriesGroup getHierarchyNode(MediaSeriesGroup parent, Object value) {
    return parent;
  }
}
