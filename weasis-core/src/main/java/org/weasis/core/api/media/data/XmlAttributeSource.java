/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.api.media.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XmlAttributeSource implements AttributeSource {
  private final Map<String, Object> attrs = new ConcurrentHashMap<>();

  public void put(String name, Object value) {
    if (name != null && value != null) {
      attrs.put(name, value);
    }
  }

  @Override
  public Object getAttribute(String name) {
    return name == null ? null : attrs.get(name);
  }
}
