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

public class SeriesEvent {
  public enum Action {
    ADD,
    REMOVE,
    UPDATE,
    UPDATE_IMAGE
  }

  private final Action action;
  private final Object source;
  private final Object param;

  public SeriesEvent(Action action, Object source, Object param) {
    this.action = action;
    this.source = source;
    this.param = param;
  }

  public Action getActionCommand() {
    return action;
  }

  public Object getSource() {
    return source;
  }

  public Object getParam() {
    return param;
  }
}
