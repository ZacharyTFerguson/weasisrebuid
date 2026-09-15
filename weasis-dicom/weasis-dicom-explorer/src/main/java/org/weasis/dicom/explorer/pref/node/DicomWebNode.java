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

import java.util.LinkedHashMap;
import java.util.Map;
import org.weasis.core.api.net.auth.AuthMethod;

/** DICOMweb node (QIDO/WADO/STOW base URL, extra headers, optional auth). */
public final class DicomWebNode {

  public enum WebType {
    WADO("WADO-URI"),
    WADO_RS("WADO-RS"),
    STOW_RS("STOW-RS"),
    QIDO_RS("QIDO-RS");

    private final String label;

    WebType(String label) {
      this.label = label;
    }

    @Override
    public String toString() {
      return label;
    }
  }

  private final String description;
  private final String baseUrl;
  private final WebType webType;
  private final Map<String, String> headers = new LinkedHashMap<>();
  private AuthMethod authMethod;

  public DicomWebNode(String description, String baseUrl) {
    this(description, baseUrl, WebType.QIDO_RS);
  }

  public DicomWebNode(String description, String baseUrl, WebType webType) {
    this.description =
        description == null || description.isBlank() ? "DICOMweb" : description.trim();
    this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
    this.webType = webType == null ? WebType.QIDO_RS : webType;
  }

  public String description() {
    return description;
  }

  public String baseUrl() {
    return baseUrl;
  }

  public WebType webType() {
    return webType;
  }

  public boolean dicomWeb() {
    return true;
  }

  public void setHeader(String name, String value) {
    if (name == null || name.isBlank()) {
      return;
    }
    headers.put(name.trim(), value == null ? "" : value);
  }

  public void setHeaders(Map<String, String> extra) {
    headers.clear();
    if (extra == null) {
      return;
    }
    extra.forEach(this::setHeader);
  }

  public Map<String, String> headers() {
    return Map.copyOf(headers);
  }

  public void setAuthMethod(AuthMethod authMethod) {
    this.authMethod = authMethod;
  }

  public AuthMethod authMethod() {
    return authMethod;
  }

  /**
   * Extra headers plus {@link AuthMethod} authorization. Auth headers win when the same name is
   * present in both maps.
   */
  public Map<String, String> requestHeaders() {
    Map<String, String> out = new LinkedHashMap<>(headers);
    if (authMethod != null && authMethod.authorizationHeaders() != null) {
      out.putAll(authMethod.authorizationHeaders());
    }
    return Map.copyOf(out);
  }

  public static boolean httpUrl(String url) {
    if (url == null || url.isBlank()) {
      return false;
    }
    String trimmed = url.trim();
    return trimmed.startsWith("http://") || trimmed.startsWith("https://");
  }

  @Override
  public String toString() {
    return description() + " (" + webType + " " + baseUrl + ")";
  }
}
