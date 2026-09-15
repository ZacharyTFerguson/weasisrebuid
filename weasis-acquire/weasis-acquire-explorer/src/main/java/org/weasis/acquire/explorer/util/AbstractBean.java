/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/** Property-change bean used by acquire tag/series models. */
public class AbstractBean<T> {

  private final PropertyChangeSupport support = new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    support.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    support.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    support.removePropertyChangeListener(listener);
  }

  public void firePropertyChange(T property, Object oldValue, Object newValue) {
    String name = property == null ? null : property.toString();
    support.firePropertyChange(name, oldValue, newValue);
  }

  public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    support.firePropertyChange(propertyName, oldValue, newValue);
  }
}
