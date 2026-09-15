/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.rt;

/** Linked RT Structure Set, Plan, and Dose for one treatment. */
public class RtSet {

  private StructureSet structureSet;
  private Plan plan;
  private Dose dose;

  public StructureSet structureSet() {
    return structureSet;
  }

  public void setStructureSet(StructureSet structureSet) {
    this.structureSet = structureSet;
  }

  public Plan plan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
  }

  public Dose dose() {
    return dose;
  }

  public void setDose(Dose dose) {
    this.dose = dose;
  }
}
