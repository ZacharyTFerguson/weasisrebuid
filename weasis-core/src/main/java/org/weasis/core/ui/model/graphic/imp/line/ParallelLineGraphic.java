/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.model.graphic.imp.line;

import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;

public class ParallelLineGraphic extends AbstractDragGraphic {

  public ParallelLineGraphic() {
    super(4);
  }

  @Override
  public void buildShape() {
    // two segments; full geometry is WP-5
    setShape(null);
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new ParallelLineGraphic();
  }
}
