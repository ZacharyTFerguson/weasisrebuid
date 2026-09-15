/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.utils.imp;

import java.util.UUID;
import org.weasis.core.ui.model.utils.UUIDable;

/** Stable identity for graphics and layers. */
public class DefaultUUID implements UUIDable {

  private String uuid;

  public DefaultUUID() {
    this(null);
  }

  public DefaultUUID(String uuid) {
    this.uuid = blank(uuid) ? UUID.randomUUID().toString() : uuid;
  }

  static boolean blank(String uuid) {
    return uuid == null || uuid.isBlank();
  }

  @Override
  public String getUuid() {
    return uuid;
  }

  @Override
  public void setUuid(String uuid) {
    if (!blank(uuid)) {
      this.uuid = uuid;
    }
  }
}
