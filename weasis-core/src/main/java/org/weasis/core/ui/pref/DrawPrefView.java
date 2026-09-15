/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.ui.pref;

/** Prefs &gt; Draw &amp; Measure. Graphic and Labels are documented SHORTCUTS.md subpages. */
public class DrawPrefView extends ShellPrefPage {

  public static final String TITLE = "Draw & Measure";

  public DrawPrefView() {
    super(TITLE, 500);
    addSubPage(new GraphicPrefView());
    addSubPage(new LabelsPrefView());
  }

  public GraphicPrefView graphicPage() {
    return (GraphicPrefView) getSubPages().getFirst();
  }

  public LabelsPrefView labelsPage() {
    return (LabelsPrefView) getSubPages().get(1);
  }
}
