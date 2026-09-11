/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.gui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JPanel;
import org.weasis.core.api.gui.Insertable;

/** Prefs (or wizard) page. Subclasses add widgets and persist on close. */
public abstract class AbstractItemDialogPage extends JPanel implements PageItem, Insertable {

  private final String title;
  private int position;
  private boolean componentEnabled = true;
  private final List<AbstractItemDialogPage> subPages = new ArrayList<>();

  protected AbstractItemDialogPage(String title, int position) {
    super(new BorderLayout());
    this.title = title == null ? "" : title;
    this.position = position;
  }

  public void addSubPage(AbstractItemDialogPage page) {
    if (page != null) {
      subPages.add(page);
    }
  }

  public List<AbstractItemDialogPage> getSubPages() {
    return Collections.unmodifiableList(subPages);
  }

  @Override
  public String toString() {
    return title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public Component getComponent() {
    return this;
  }

  @Override
  public String getComponentName() {
    return title;
  }

  @Override
  public Type getType() {
    return Type.PREFERENCES;
  }

  @Override
  public int getComponentPosition() {
    return position;
  }

  @Override
  public void setComponentPosition(int position) {
    this.position = position;
  }

  @Override
  public boolean isComponentEnabled() {
    return componentEnabled;
  }

  @Override
  public void setComponentEnabled(boolean enabled) {
    this.componentEnabled = enabled;
  }
}
