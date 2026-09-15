/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.weasis.core.api.explorer.ObservableEvent;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.media.data.Codec;

/**
 * Patient → Study → Series grouping. Studies join one patient only when Patient Name <b>and</b>
 * Patient ID both match.
 */
public class DicomModel implements DataExplorerModel {

  private final List<ImportedInstance> instances = new CopyOnWriteArrayList<>();
  private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
  private Codec[] codecs = new Codec[0];

  @Override
  public Codec[] getCodecPlugins() {
    return codecs;
  }

  public void setCodecPlugins(Codec[] codecs) {
    this.codecs = codecs == null ? new Codec[0] : codecs;
  }

  public boolean addInstance(ImportedInstance inst) {
    if (inst == null || instances.contains(inst)) {
      return false;
    }
    instances.add(inst);
    firePropertyChange(new ObservableEvent(ObservableEvent.BasicAction.ADD, this, inst));
    return true;
  }

  public List<ImportedInstance> getInstances() {
    return List.copyOf(instances);
  }

  public Map<String, List<ImportedInstance>> patients() {
    Map<String, List<ImportedInstance>> map = new LinkedHashMap<>();
    for (ImportedInstance inst : instances) {
      map.computeIfAbsent(inst.patientKey(), k -> new ArrayList<>()).add(inst);
    }
    return map;
  }

  public Map<String, List<ImportedInstance>> studies(String patientKey) {
    Map<String, List<ImportedInstance>> map = new LinkedHashMap<>();
    for (ImportedInstance inst : patients().getOrDefault(patientKey, List.of())) {
      map.computeIfAbsent(inst.studyUid(), k -> new ArrayList<>()).add(inst);
    }
    return map;
  }

  public static boolean samePatient(ImportedInstance a, ImportedInstance b) {
    return a != null
        && b != null
        && Objects.equals(a.patientName(), b.patientName())
        && Objects.equals(a.patientId(), b.patientId());
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
    pcs.firePropertyChange(event.getActionCommand().name(), null, event.getNewValue());
  }
}
