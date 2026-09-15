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

import jakarta.xml.bind.annotation.XmlRootElement;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.List;
import org.weasis.core.api.image.measure.MeasurementsAdapter;
import org.weasis.core.api.image.util.Unit;
import org.weasis.core.ui.model.graphic.AbstractDragGraphic;
import org.weasis.core.ui.model.graphic.AbstractGraphic;
import org.weasis.core.ui.model.utils.bean.MeasureItem;
import org.weasis.core.ui.model.utils.bean.Measurement;

@XmlRootElement(name = "LineGraphic")
public class LineGraphic extends AbstractDragGraphic {

  public static final Measurement LENGTH = new Measurement("Length", 1, true);

  public LineGraphic() {
    super(2);
  }

  public double getLength() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      return 0;
    }
    return a.distance(b);
  }

  public List<MeasureItem> computeMeasurements(MeasurementsAdapter adapter) {
    MeasurementsAdapter used = adapter == null ? new MeasurementsAdapter(1.0, Unit.PIXEL) : adapter;
    return List.of(
        new MeasureItem(LENGTH, used.getLength(getLength()), used.getUnit().getSymbol()));
  }

  @Override
  public void buildShape() {
    Point2D.Double a = getHandlePoint(0);
    Point2D.Double b = getHandlePoint(1);
    if (a == null || b == null) {
      setShape(null);
      return;
    }
    setShape(new Line2D.Double(a, b));
  }

  @Override
  protected AbstractGraphic newInstance() {
    return new LineGraphic();
  }
}
