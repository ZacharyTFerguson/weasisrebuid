/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package org.weasis.core.ui.tp.raven.datetime.component.time;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.weasis.core.ui.tp.raven.datetime.component.time.event.TimeSelectionModelEvent;
import org.weasis.core.ui.tp.raven.datetime.component.time.event.TimeSelectionModelListener;

public class TimeSelectionModel {
  private LocalTime time = LocalTime.NOON;
  private final List<TimeSelectionModelListener> listeners = new ArrayList<>();

  public LocalTime getTime() {
    return time;
  }

  public void setTime(LocalTime time) {
    this.time = time == null ? LocalTime.NOON : time;
    TimeSelectionModelEvent event = new TimeSelectionModelEvent(this, this.time);
    for (TimeSelectionModelListener l : List.copyOf(listeners)) {
      l.timeSelectionModelChanged(event);
    }
  }

  public void addTimeSelectionModelListener(TimeSelectionModelListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  public void removeTimeSelectionModelListener(TimeSelectionModelListener listener) {
    listeners.remove(listener);
  }
}
