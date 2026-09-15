/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.acquire.explorer;

import java.util.concurrent.atomic.AtomicReference;

/** Per-command store so Surefire {@code parallel=all} tests do not share mutable statics. */
public final class AcquirePatientStore {

  private final AtomicReference<PatientDemographics> current =
      new AtomicReference<>(PatientDemographics.empty());

  public PatientDemographics get() {
    return current.get();
  }

  public void set(PatientDemographics demographics) {
    current.set(demographics == null ? PatientDemographics.empty() : demographics);
  }
}
