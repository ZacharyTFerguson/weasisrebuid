/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.base.explorer.list;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Thumbnail list model backed by paths from a directory listing. */
public interface IThumbnailModel {

  void loadDirectory(Path directory) throws IOException;

  void setItems(List<Path> items);

  int getSize();

  Path getElementAt(int index);

  List<Path> items();

  void clear();
}
