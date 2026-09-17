/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.viewer2d.mpr.pref;

import org.weasis.core.ui.pref.ShellPrefPage;

/** Prefs &gt; MPR. Title-only: PREFERENCES.md has no MPR persist keys. */
public class MprPrefView extends ShellPrefPage {

  public static final String TITLE = "MPR";

  public MprPrefView() {
    super(TITLE, 420);
  }
}
