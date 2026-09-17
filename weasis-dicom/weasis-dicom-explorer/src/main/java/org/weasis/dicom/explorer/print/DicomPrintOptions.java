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

/** Basic Film Session / Film Box / Image Box options (DICOM PS3.4 Print Management). */
public final class DicomPrintOptions {

  public enum FilmOrientation {
    PORTRAIT,
    LANDSCAPE
  }

  public enum FilmSize {
    SIZE_8INX10IN("8INX10IN"),
    SIZE_10INX12IN("10INX12IN"),
    SIZE_14INX17IN("14INX17IN");

    private final String dicomId;

    FilmSize(String dicomId) {
      this.dicomId = dicomId;
    }

    public String dicomId() {
      return dicomId;
    }
  }

  public enum PrintPriority {
    HIGH,
    MED,
    LOW
  }

  public enum MediumType {
    PAPER,
    BLUE_FILM,
    CLEAR_FILM
  }

  private FilmOrientation orientation = FilmOrientation.PORTRAIT;
  private FilmSize filmSize = FilmSize.SIZE_8INX10IN;
  private int copies = 1;
  private PrintPriority printPriority = PrintPriority.MED;
  private MediumType mediumType = MediumType.BLUE_FILM;
  private String filmDestination = "PROCESSOR";
  private String imageDisplayFormat = "STANDARD\\1,1";
  private String magnificationType = "BILINEAR";
  private String polarity = "NORMAL";
  private boolean color;

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

  public PrintPriority printPriority() {
    return printPriority;
  }

  public void setPrintPriority(PrintPriority printPriority) {
    this.printPriority = printPriority == null ? PrintPriority.MED : printPriority;
  }

  public MediumType mediumType() {
    return mediumType;
  }

  public void setMediumType(MediumType mediumType) {
    this.mediumType = mediumType == null ? MediumType.BLUE_FILM : mediumType;
  }

  public String filmDestination() {
    return filmDestination;
  }

  public void setFilmDestination(String filmDestination) {
    this.filmDestination =
        filmDestination == null || filmDestination.isBlank() ? "PROCESSOR" : filmDestination;
  }

  public String imageDisplayFormat() {
    return imageDisplayFormat;
  }

  public void setImageDisplayFormat(String imageDisplayFormat) {
    this.imageDisplayFormat =
        imageDisplayFormat == null || imageDisplayFormat.isBlank()
            ? "STANDARD\\1,1"
            : imageDisplayFormat;
  }

  public String magnificationType() {
    return magnificationType;
  }

  public void setMagnificationType(String magnificationType) {
    this.magnificationType =
        magnificationType == null || magnificationType.isBlank() ? "BILINEAR" : magnificationType;
  }

  public String polarity() {
    return polarity;
  }

  public void setPolarity(String polarity) {
    this.polarity = polarity == null || polarity.isBlank() ? "NORMAL" : polarity;
  }

  public boolean color() {
    return color;
  }

  public void setColor(boolean color) {
    this.color = color;
  }

  public String mediumTypeDicom() {
    return mediumType.name().replace('_', ' ');
  }

  /** Film Session N-CREATE module. Orientation and size belong on the Film Box. */
  public Attributes toFilmSessionAttributes() {
    Attributes attrs = new Attributes();
    attrs.setInt(Tag.NumberOfCopies, VR.IS, copies);
    attrs.setString(Tag.PrintPriority, VR.CS, printPriority.name());
    attrs.setString(Tag.MediumType, VR.CS, mediumTypeDicom());
    attrs.setString(Tag.FilmDestination, VR.CS, filmDestination);
    return attrs;
  }
}
