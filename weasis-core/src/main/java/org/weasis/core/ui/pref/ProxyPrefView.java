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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.weasis.core.api.gui.util.AbstractItemDialogPage;
import org.weasis.core.api.service.UICore;
import org.weasis.core.api.service.WProperties;

/**
 * Prefs &gt; Proxy Server. Manual form has HTTP / HTTPS / FTP / SOCKS + Exceptions and <em>no</em>
 * user/password fields ({@code ProxyPrefView} never stores {@code proxy.auth.pwd}).
 */
public class ProxyPrefView extends AbstractItemDialogPage {

  public static final String AUTH_PWD_LEFTOVER = "proxy.auth.pwd";

  private final WProperties prefs;
  private final JRadioButton direct = new JRadioButton("Direct connection (no proxy)");
  private final JRadioButton manual = new JRadioButton("Manual proxy configuration");
  private final JTextField httpHost = new JTextField();
  private final JTextField httpPort = new JTextField();
  private final JTextField httpsHost = new JTextField();
  private final JTextField httpsPort = new JTextField();
  private final JTextField ftpHost = new JTextField();
  private final JTextField ftpPort = new JTextField();
  private final JTextField socksHost = new JTextField();
  private final JTextField socksPort = new JTextField();
  private final JTextField exceptions = new JTextField();

  public ProxyPrefView() {
    this(UICore.getInstance().getSystemPreferences());
  }

  public ProxyPrefView(WProperties prefs) {
    super("Proxy Server", 700);
    this.prefs = prefs;
    prefs.remove(AUTH_PWD_LEFTOVER);
    initGui();
    load();
  }

  private void initGui() {
    ButtonGroup group = new ButtonGroup();
    group.add(direct);
    group.add(manual);
    JPanel top = new JPanel(new GridLayout(0, 1));
    top.add(direct);
    top.add(manual);
    JPanel fields = new JPanel(new GridLayout(0, 2, 6, 4));
    fields.add(new JLabel("HTTP"));
    fields.add(row(httpHost, httpPort, ProxySettings.DEFAULT_HTTP_PORT));
    fields.add(new JLabel("HTTPS"));
    fields.add(row(httpsHost, httpsPort, ProxySettings.DEFAULT_HTTPS_PORT));
    fields.add(new JLabel("FTP"));
    fields.add(row(ftpHost, ftpPort, ProxySettings.DEFAULT_FTP_PORT));
    fields.add(new JLabel("SOCKS"));
    fields.add(row(socksHost, socksPort, ProxySettings.DEFAULT_SOCKS_PORT));
    fields.add(new JLabel("Exceptions"));
    fields.add(exceptions);
    add(top, BorderLayout.NORTH);
    add(fields, BorderLayout.CENTER);
    manual.addActionListener(e -> setFieldsEnabled(true));
    direct.addActionListener(e -> setFieldsEnabled(false));
  }

  private static JPanel row(JTextField host, JTextField port, int defaultPort) {
    JPanel p = new JPanel(new GridLayout(1, 2, 4, 0));
    host.setName("host");
    port.setName("port");
    port.setToolTipText("Default " + defaultPort + " when empty");
    p.add(host);
    p.add(port);
    return p;
  }

  private void load() {
    boolean man = ProxySettings.isManual(prefs);
    manual.setSelected(man);
    direct.setSelected(!man);
    httpHost.setText(prefs.getProperty(ProxySettings.HTTP_HOST, ""));
    httpPort.setText(prefs.getProperty(ProxySettings.HTTP_PORT, ""));
    httpsHost.setText(prefs.getProperty(ProxySettings.HTTPS_HOST, ""));
    httpsPort.setText(prefs.getProperty(ProxySettings.HTTPS_PORT, ""));
    ftpHost.setText(prefs.getProperty(ProxySettings.FTP_HOST, ""));
    ftpPort.setText(prefs.getProperty(ProxySettings.FTP_PORT, ""));
    socksHost.setText(prefs.getProperty(ProxySettings.SOCKS_HOST, ""));
    socksPort.setText(prefs.getProperty(ProxySettings.SOCKS_PORT, ""));
    exceptions.setText(prefs.getProperty(ProxySettings.EXCEPTIONS, ""));
    setFieldsEnabled(man);
  }

  private void setFieldsEnabled(boolean enabled) {
    httpHost.setEnabled(enabled);
    httpPort.setEnabled(enabled);
    httpsHost.setEnabled(enabled);
    httpsPort.setEnabled(enabled);
    ftpHost.setEnabled(enabled);
    ftpPort.setEnabled(enabled);
    socksHost.setEnabled(enabled);
    socksPort.setEnabled(enabled);
    exceptions.setEnabled(enabled);
  }

  public boolean hasUserPasswordFields() {
    return false;
  }

  public JRadioButton getDirectButton() {
    return direct;
  }

  public JRadioButton getManualButton() {
    return manual;
  }

  @Override
  public void closeAdditionalWindow() {
    prefs.put(
        ProxySettings.TYPE,
        manual.isSelected() ? ProxySettings.TYPE_MANUAL : ProxySettings.TYPE_DIRECT);
    prefs.put(ProxySettings.HTTP_HOST, httpHost.getText());
    prefs.put(ProxySettings.HTTP_PORT, httpPort.getText());
    prefs.put(ProxySettings.HTTPS_HOST, httpsHost.getText());
    prefs.put(ProxySettings.HTTPS_PORT, httpsPort.getText());
    prefs.put(ProxySettings.FTP_HOST, ftpHost.getText());
    prefs.put(ProxySettings.FTP_PORT, ftpPort.getText());
    prefs.put(ProxySettings.SOCKS_HOST, socksHost.getText());
    prefs.put(ProxySettings.SOCKS_PORT, socksPort.getText());
    prefs.put(ProxySettings.EXCEPTIONS, exceptions.getText());
    prefs.remove(AUTH_PWD_LEFTOVER);
    ProxySettings.applyAll(prefs, System.getProperties());
  }

  @Override
  public void resetToDefaultValues() {
    prefs.put(ProxySettings.TYPE, ProxySettings.TYPE_DIRECT);
    load();
  }
}
