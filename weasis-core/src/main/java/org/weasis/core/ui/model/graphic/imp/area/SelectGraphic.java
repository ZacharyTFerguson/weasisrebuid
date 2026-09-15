/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.area;

import org.weasis.core.ui.model.graphic.AbstractGraphic;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Rubber-band selection rectangle (not a measurement). */
@XmlRootElement(name = "SelectGraphic")
public class SelectGraphic extends RectangleGraphic {

  @Override
  protected AbstractGraphic newInstance() {
    return new SelectGraphic();
  }
}
