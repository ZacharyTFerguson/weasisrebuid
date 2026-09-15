/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.dockable.components.actions;

import java.awt.image.BufferedImage;
import org.weasis.acquire.explorer.AcquireImageValues;

/** Editor session: source image plus pending {@link AcquireImageValues}. */
public class AbstractAcquireAction {

  private final AcquireAction action = new AcquireAction();
  private AcquireImageValues values = new AcquireImageValues();
  private BufferedImage source;

  public AcquireImageValues values() {
    return values;
  }

  public void setValues(AcquireImageValues values) {
    this.values = values == null ? new AcquireImageValues() : values;
  }

  public BufferedImage getSource() {
    return source;
  }

  public void setSource(BufferedImage source) {
    this.source = source;
  }

  public BufferedImage apply() {
    return action.apply(source, values);
  }
}
