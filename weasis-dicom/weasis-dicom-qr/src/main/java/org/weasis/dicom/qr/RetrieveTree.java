/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.qr;

import java.util.List;
import javax.swing.JTree;
import org.dcm4che3.data.Attributes;

/** Query result tree with a {@link RetrieveSelection} of checked studies and series. */
public class RetrieveTree extends JTree {

  private final RetrieveTreeModel retrieveModel;
  private final RetrieveSelection selection = new RetrieveSelection();

  public RetrieveTree() {
    this(new RetrieveTreeModel());
  }

  public RetrieveTree(RetrieveTreeModel model) {
    super(model);
    this.retrieveModel = model;
  }

  public RetrieveTreeModel retrieveModel() {
    return retrieveModel;
  }

  public RetrieveSelection selection() {
    return selection;
  }

  public void checkStudy(String studyUid) {
    selection.checkStudy(studyUid);
  }

  public void checkSeries(String studyUid, String seriesUid) {
    selection.checkSeries(studyUid, seriesUid);
  }

  public void loadFindResults(List<Attributes> findResults) {
    retrieveModel.addCFindResults(findResults);
  }

  public void clear() {
    retrieveModel.clearResults();
    selection.clear();
  }
}
