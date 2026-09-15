/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.docking;

import java.util.Hashtable;
import org.weasis.core.api.gui.Insertable;
import org.weasis.core.api.gui.InsertableFactory;

public abstract class ExtToolFactory implements InsertableFactory {
  @Override
  public abstract Insertable createInstance(Hashtable<String, Object> properties);

  @Override
  public void dispose(Insertable component) {}

  @Override
  public Insertable.Type getType() {
    return Insertable.Type.TOOL;
  }
}
