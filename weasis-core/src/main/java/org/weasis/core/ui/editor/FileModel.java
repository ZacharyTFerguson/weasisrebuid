/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.editor;

import org.weasis.core.api.explorer.model.AbstractFileModel;
import org.weasis.core.api.media.data.MediaSeriesGroup;
import org.weasis.core.api.media.data.MediaSeriesGroupNode;
import org.weasis.core.api.media.data.TagW;

/** Non-DICOM explorer tree: patient / study / series / image groups. */
public class FileModel extends AbstractFileModel {

  public MediaSeriesGroupNode addGroup(MediaSeriesGroup parent, TagW tag, Object id) {
    MediaSeriesGroupNode node = new MediaSeriesGroupNode(tag, id);
    addHierarchyNode(parent, node);
    return node;
  }
}
