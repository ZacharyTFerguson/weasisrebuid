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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.weasis.core.api.explorer.ObservableEvent;
import org.weasis.core.api.media.data.Codec;
import org.weasis.core.api.media.data.MediaSeriesGroup;
import org.weasis.core.api.media.data.MediaSeriesGroupNode;
import org.weasis.core.api.media.data.TagW;

public abstract class AbstractFileModel implements DataExplorerModel, TreeModel {
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private final MediaSeriesGroupNode root = new MediaSeriesGroupNode(TagW.PatientID, "root");
  private final List<Codec> codecs = new ArrayList<>();
  private final Map<MediaSeriesGroup, List<MediaSeriesGroup>> children = new LinkedHashMap<>();

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

  public boolean addHierarchyNode(MediaSeriesGroup parent, MediaSeriesGroup child) {
    if (child == null) {
      return false;
    }
    return storeChild(parent == null ? root : parent, child);
  }

  public boolean removeHierarchyNode(MediaSeriesGroup parent, MediaSeriesGroup child) {
    List<MediaSeriesGroup> kids = children.get(parent == null ? root : parent);
    if (kids == null || !kids.remove(child)) {
      return false;
    }
    firePropertyChange(new ObservableEvent(ObservableEvent.BasicAction.REMOVE, this, child));
    return true;
  }

  boolean storeChild(MediaSeriesGroup parent, MediaSeriesGroup child) {
    List<MediaSeriesGroup> kids = children.computeIfAbsent(parent, k -> new ArrayList<>());
    if (kids.contains(child)) {
      return false;
    }
    kids.add(child);
    firePropertyChange(new ObservableEvent(ObservableEvent.BasicAction.ADD, this, child));
    return true;
  }

  @Override
  public Collection<MediaSeriesGroup> getChildren(MediaSeriesGroup node) {
    List<MediaSeriesGroup> kids = children.get(node == null ? root : node);
    return kids == null ? List.of() : List.copyOf(kids);
  }

  @Override
  public MediaSeriesGroup getHierarchyNode(MediaSeriesGroup parent, Object value) {
    for (MediaSeriesGroup child : getChildren(parent == null ? root : parent)) {
      if (sameId(child, value)) {
        return child;
      }
    }
    return null;
  }

  static boolean sameId(MediaSeriesGroup child, Object value) {
    if (child == null) {
      return false;
    }
    TagW id = child.getTagID();
    return id != null && Objects.equals(child.getTagValue(id), value);
  }
}
