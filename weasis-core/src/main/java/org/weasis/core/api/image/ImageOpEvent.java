/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.image;

/** Fired when an op graph finishes or a node param changes. */
public class ImageOpEvent {

  public enum Type {
    RESET_DISPLAY,
    APPLY,
    IMAGE_CHANGE,
    SERIES_CHANGE
  }

  private final Type eventType;
  private final Object source;

  public ImageOpEvent(Type eventType, Object source) {
    this.eventType = eventType;
    this.source = source;
  }

  public Type getEventType() {
    return eventType;
  }

  public Object getSource() {
    return source;
  }
}
