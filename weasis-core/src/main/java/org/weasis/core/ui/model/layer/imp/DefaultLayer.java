/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.layer.imp;

import org.weasis.core.ui.model.layer.Layer;
import org.weasis.core.ui.model.layer.LayerType;
import org.weasis.core.ui.model.utils.UUIDable;
import org.weasis.core.ui.model.utils.imp.DefaultUUID;

/** Named canvas layer with a UUID and a locked flag. */
public class DefaultLayer extends Layer implements UUIDable {

  private final DefaultUUID id = new DefaultUUID();
  private boolean locked;

  public DefaultLayer() {}

  public DefaultLayer(LayerType type) {
    super(type);
  }

  @Override
  public String getUuid() {
    return id.getUuid();
  }

  @Override
  public void setUuid(String uuid) {
    id.setUuid(uuid);
  }

  public boolean isLocked() {
    return locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }
}
