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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.VR;

/**
 * Builds C-MOVE / C-GET identifiers from a checked retrieve tree. Does not open sockets; Have tests
 * inject the selection.
 */
public class RetrieveTask {

  private final RetrieveContext context;
  private final RetrieveSelection selection;
  private final GracefulCancel cancel;
  private final List<Identifier> identifiers = new ArrayList<>();

  public RetrieveTask(RetrieveContext context, RetrieveSelection selection, GracefulCancel cancel) {
    this.context = context == null ? new RetrieveContext() : context;
    this.selection = selection == null ? new RetrieveSelection() : selection;
    this.cancel = cancel == null ? new GracefulCancel() : cancel;
  }

  public void run() {
    identifiers.clear();
    if (cancel.isCancelled()) {
      return;
    }
    for (String studyUid : selection.checkedStudies()) {
      if (cancel.isCancelled()) {
        return;
      }
      identifiers.add(
          Identifier.study(studyUid, context.method(), SearchParameters.QueryRetrieveLevel.STUDY));
    }
    for (String key : selection.checkedSeries()) {
      if (cancel.isCancelled()) {
        return;
      }
      int slash = key.indexOf('/');
      String studyUid = slash < 0 ? key : key.substring(0, slash);
      String seriesUid = slash < 0 ? "" : key.substring(slash + 1);
      if (selection.retrievesAllSeries(studyUid)) {
        continue;
      }
      identifiers.add(
          Identifier.series(
              studyUid, seriesUid, context.method(), SearchParameters.QueryRetrieveLevel.SERIES));
    }
  }

  public List<Identifier> identifiers() {
    return Collections.unmodifiableList(identifiers);
  }

  public boolean isCancelled() {
    return cancel.isCancelled();
  }

  public GracefulCancel cancel() {
    return cancel;
  }

  /** One DIMSE retrieve identifier (study-root). */
  public static final class Identifier {

    private final SearchParameters.QueryRetrieveLevel level;
    private final String studyUid;
    private final String seriesUid;
    private final RetrieveContext.RetrieveMethod method;

    Identifier(
        SearchParameters.QueryRetrieveLevel level,
        String studyUid,
        String seriesUid,
        RetrieveContext.RetrieveMethod method) {
      this.level = level;
      this.studyUid = studyUid == null ? "" : studyUid;
      this.seriesUid = seriesUid == null ? "" : seriesUid;
      this.method = method == null ? RetrieveContext.RetrieveMethod.C_MOVE : method;
    }

    static Identifier study(
        String studyUid,
        RetrieveContext.RetrieveMethod method,
        SearchParameters.QueryRetrieveLevel level) {
      return new Identifier(level, studyUid, "", method);
    }

    static Identifier series(
        String studyUid,
        String seriesUid,
        RetrieveContext.RetrieveMethod method,
        SearchParameters.QueryRetrieveLevel level) {
      return new Identifier(level, studyUid, seriesUid, method);
    }

    public SearchParameters.QueryRetrieveLevel level() {
      return level;
    }

    public String studyUid() {
      return studyUid;
    }

    public String seriesUid() {
      return seriesUid;
    }

    public RetrieveContext.RetrieveMethod method() {
      return method;
    }

    public String sopClassUid() {
      if (method == RetrieveContext.RetrieveMethod.C_GET) {
        return UID.StudyRootQueryRetrieveInformationModelGet;
      }
      return UID.StudyRootQueryRetrieveInformationModelMove;
    }

    public Attributes keys() {
      Attributes keys = new Attributes();
      keys.setString(Tag.QueryRetrieveLevel, VR.CS, level.name());
      keys.setString(Tag.StudyInstanceUID, VR.UI, studyUid);
      if (level == SearchParameters.QueryRetrieveLevel.SERIES && !seriesUid.isBlank()) {
        keys.setString(Tag.SeriesInstanceUID, VR.UI, seriesUid);
      }
      return keys;
    }
  }
}
