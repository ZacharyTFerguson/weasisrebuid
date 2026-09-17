/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.util;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LimitedTextField extends JTextField {
  public LimitedTextField(int limit) {
    super(new LimitDoc(limit), "", limit);
  }

  static final class LimitDoc extends PlainDocument {
    private final int limit;

    LimitDoc(int limit) {
      this.limit = Math.max(1, limit);
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
      if (str == null) {
        return;
      }
      if (getLength() + str.length() <= limit) {
        super.insertString(offs, str, a);
      }
    }
  }
}
