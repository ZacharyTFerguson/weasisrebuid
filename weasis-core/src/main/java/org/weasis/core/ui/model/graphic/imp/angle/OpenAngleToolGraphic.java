/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.angle;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

/** Open angle shares Cobb geometry (two rays). */
@XmlRootElement(name = "OpenAngleToolGraphic")
public class OpenAngleToolGraphic extends CobbAngleToolGraphic {

  @Override
  protected AbstractGraphic newInstance() {
    return new OpenAngleToolGraphic();
  }
}
