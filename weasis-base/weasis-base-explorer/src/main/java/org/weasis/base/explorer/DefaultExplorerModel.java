/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.weasis.core.api.explorer.ObservableEvent;
import org.weasis.core.api.explorer.model.DataExplorerModel;
import org.weasis.core.api.media.data.Codec;

public class DefaultExplorerModel implements DataExplorerModel {

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  @Override
  public Codec[] getCodecPlugins() {
    return new Codec[0];
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  @Override
  public void firePropertyChange(ObservableEvent event) {
    support.firePropertyChange(event.getActionCommand().name(), null, event.getNewValue());
  }
}
