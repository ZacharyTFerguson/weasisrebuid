/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list.impl;

import java.nio.file.Path;
import java.util.List;
import org.weasis.base.explorer.list.AThumbnailModel;
import org.weasis.base.explorer.list.DiskFileList;
import org.weasis.base.explorer.list.ListObservable;

/** Thumbnail list model that observes disk listings for the non-DICOM explorer. */
public class JIListModel extends AThumbnailModel {

  private final ListObservable observable = new ListObservable();

  public JIListModel() {
    super();
  }

  public JIListModel(DiskFileList disk) {
    super(disk);
  }

  public ListObservable observable() {
    return observable;
  }

  @Override
  public void setItems(List<Path> items) {
    super.setItems(items);
    observable.fireListChanged();
  }

  @Override
  public void clear() {
    super.clear();
    observable.fireListChanged();
  }
}
