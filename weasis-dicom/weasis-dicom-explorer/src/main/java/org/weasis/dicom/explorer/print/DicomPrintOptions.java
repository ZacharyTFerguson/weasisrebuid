/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.print;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;

/** Basic DICOM print film session options. */
public final class DicomPrintOptions {

  public enum FilmOrientation {
    PORTRAIT,
    LANDSCAPE
  }

  public enum FilmSize {
    SIZE_8INX10IN,
    SIZE_10INX12IN,
    SIZE_14INX17IN
  }

  private FilmOrientation orientation = FilmOrientation.PORTRAIT;
  private FilmSize filmSize = FilmSize.SIZE_8INX10IN;
  private int copies = 1;

  public FilmOrientation orientation() {
    return orientation;
  }

  public void setOrientation(FilmOrientation orientation) {
    this.orientation = orientation == null ? FilmOrientation.PORTRAIT : orientation;
  }

  public FilmSize filmSize() {
    return filmSize;
  }

  public void setFilmSize(FilmSize filmSize) {
    this.filmSize = filmSize == null ? FilmSize.SIZE_8INX10IN : filmSize;
  }

  public int copies() {
    return copies;
  }

  public void setCopies(int copies) {
    this.copies = Math.max(1, copies);
  }

  public Attributes toFilmSessionAttributes() {
    Attributes attrs = new Attributes();
    attrs.setString(Tag.FilmOrientation, VR.CS, orientation.name());
    attrs.setString(Tag.FilmSizeID, VR.CS, filmSize.name().replace('_', ' '));
    attrs.setInt(Tag.NumberOfCopies, VR.IS, copies);
    return attrs;
  }
}
