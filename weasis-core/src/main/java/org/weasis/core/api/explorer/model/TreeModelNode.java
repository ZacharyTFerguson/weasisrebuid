/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.explorer.model;

import org.weasis.core.api.media.data.TagW;

public class TreeModelNode {
  public static final TreeModelNode PATIENT = new TreeModelNode(1, TagW.PatientID);
  public static final TreeModelNode STUDY = new TreeModelNode(2, TagW.StudyInstanceUID);
  public static final TreeModelNode SERIES = new TreeModelNode(3, TagW.SeriesInstanceUID);
  public static final TreeModelNode IMAGE = new TreeModelNode(4, TagW.SOPInstanceUID);

  private final int depth;
  private final TagW tagID;

  public TreeModelNode(int depth, TagW tagID) {
    this.depth = depth;
    this.tagID = tagID;
  }

  public int getDepth() {
    return depth;
  }

  public TagW getTagID() {
    return tagID;
  }
}
