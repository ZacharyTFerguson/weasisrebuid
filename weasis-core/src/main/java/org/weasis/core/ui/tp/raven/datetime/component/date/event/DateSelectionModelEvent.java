/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.tp.raven.datetime.component.date.event;

import java.util.EventObject;

public class DateSelectionModelEvent extends EventObject {
  private final Object value;

  public DateSelectionModelEvent(Object source) {
    this(source, null);
  }

  public DateSelectionModelEvent(Object source, Object value) {
    super(source == null ? DateSelectionModelEvent.class : source);
    this.value = value;
  }

  public Object getValue() {
    return value;
  }
}
