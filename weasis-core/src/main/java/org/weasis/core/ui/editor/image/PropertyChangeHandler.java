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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/** Last view property change (zoom, and other JComponent fires). */
public class PropertyChangeHandler implements PropertyChangeListener {

  private PropertyChangeEvent last;

  public void bind(DefaultView2d<?> view) {
    if (view != null) {
      view.addPropertyChangeListener(this);
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    this.last = evt;
  }

  public PropertyChangeEvent last() {
    return last;
  }

  public boolean saw(String name) {
    return last != null && name.equals(last.getPropertyName());
  }
}
