/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.dicom.explorer.pref.node;

import org.weasis.core.api.net.auth.AuthMethod;
import org.weasis.core.api.service.WProperties;

/** List of DICOMweb authentication methods, persisted through {@link AuthenticationPersistence}. */
public class AuthenticationEditor extends AbstractListEditor<AuthMethod> {

  private final AuthMethodDialog dialog = new AuthMethodDialog();

  public AuthMethodDialog dialog() {
    return dialog;
  }

  @Override
  public AuthMethod createItem() {
    return dialog.apply();
  }

  @Override
  public AuthMethod modifyItem(AuthMethod current) {
    if (current != null
        && (dialog.idField().getText() == null || dialog.idField().getText().isBlank())) {
      dialog.load(current);
    }
    return dialog.apply();
  }

  public void loadFrom(WProperties prefs) {
    clearItems();
    for (AuthMethod method : AuthenticationPersistence.load(prefs)) {
      addElement(method);
    }
  }

  public void saveTo(WProperties prefs) {
    AuthenticationPersistence.save(prefs, items());
  }
}
