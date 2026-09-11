/*
 * Copyright (c) 2026 Weasis rebuild contributors.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0, or the Apache
 * License, Version 2.0 which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package org.weasis.core.api.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * {@code $dicom:rs} requires {@code -u URL}. Optional {@code -r}, {@code -H}, headers,
 * query/retrieve extensions. Default {@code --accept-ext} is {@code transfer-syntax=*}.
 */
public final class DicomRsArgs {

  public static final String DEFAULT_ACCEPT_EXT = "transfer-syntax=*";
  public static final String MISSING_URL = "$dicom:rs requires -u URL";

  private final String url;
  private final String retrieve;
  private final List<String> headers;
  private final String queryExt;
  private final String retrieveExt;
  private final String acceptExt;
  private final boolean showWholeStudy;

  public DicomRsArgs(
      String url,
      String retrieve,
      List<String> headers,
      String queryExt,
      String retrieveExt,
      String acceptExt,
      boolean showWholeStudy) {
    this.url = url;
    this.retrieve = retrieve;
    this.headers = headers == null ? List.of() : List.copyOf(headers);
    this.queryExt = queryExt;
    this.retrieveExt = retrieveExt;
    this.acceptExt = acceptExt == null || acceptExt.isBlank() ? DEFAULT_ACCEPT_EXT : acceptExt;
    this.showWholeStudy = showWholeStudy;
  }

  public String url() {
    return url;
  }

  public String retrieve() {
    return retrieve;
  }

  public List<String> headers() {
    return headers;
  }

  public String queryExt() {
    return queryExt;
  }

  public String retrieveExt() {
    return retrieveExt;
  }

  public String acceptExt() {
    return acceptExt;
  }

  public boolean showWholeStudy() {
    return showWholeStudy;
  }

  public static DicomRsArgs parse(String... args) {
    List<String> tokens = CommandTokens.of(args);
    if (!tokens.isEmpty()) {
      String first = tokens.getFirst();
      if (first.startsWith("$")) {
        first = first.substring(1);
      }
      if (first.toLowerCase(Locale.ROOT).startsWith("dicom:rs")) {
        tokens = tokens.subList(1, tokens.size());
      }
    }
    String url = null;
    String retrieve = null;
    List<String> headers = new ArrayList<>();
    String queryExt = null;
    String retrieveExt = null;
    String acceptExt = DEFAULT_ACCEPT_EXT;
    boolean showWhole = false;
    for (int i = 0; i < tokens.size(); i++) {
      String t = tokens.get(i);
      String next = i + 1 < tokens.size() ? tokens.get(i + 1) : null;
      switch (t) {
        case "-u" -> url = next;
        case "-r" -> retrieve = next;
        case "-H", "--query-header", "--retrieve-header" -> {
          if (next != null) {
            headers.add(next);
          }
        }
        case "--query-ext" -> queryExt = next;
        case "--retrieve-ext" -> retrieveExt = next;
        case "--accept-ext" -> acceptExt = next;
        case "--show-whole-study" -> showWhole = true;
        default -> {
          // skip
        }
      }
    }
    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException(MISSING_URL);
    }
    return new DicomRsArgs(url, retrieve, headers, queryExt, retrieveExt, acceptExt, showWhole);
  }
}
