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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.weasis.core.api.net.auth.AuthMethod;
import org.weasis.core.api.net.auth.DefaultAuthMethod;
import org.weasis.core.api.service.WProperties;
import org.weasis.dicom.explorer.rs.RsQueryParams;

class DicomNodeDialogHaveTest {

  @Test
  void dimseDialogDefaultsToDcm4cheePort11112() {
    DicomNodeDialog dialog = new DicomNodeDialog();
    assertEquals("DICOM Node", dialog.getTitle());
    DefaultDicomNode node = dialog.apply();
    assertEquals("DCM4CHEE", node.aeTitle());
    assertEquals("localhost", node.host());
    assertEquals(11112, node.port());
    assertEquals("DCM4CHEE@localhost:11112", node.endpoint());
  }

  @Test
  void dimseDialogBlankAndInvalidPortUseDefaults() {
    DicomNodeDialog dialog = new DicomNodeDialog();
    dialog.setDescription("");
    dialog.setAeTitle("  ");
    dialog.setHost("");
    dialog.setPortText("not-a-port");
    DefaultDicomNode node = dialog.apply();
    assertEquals("DCM4CHEE", node.description());
    assertEquals("DCM4CHEE", node.aeTitle());
    assertEquals("localhost", node.host());
    assertEquals(11112, node.port());

    dialog.setAeTitle("ORTHANC");
    dialog.setHost("pacs.example");
    dialog.setPortText("99999");
    node = dialog.apply();
    assertEquals("ORTHANC", node.aeTitle());
    assertEquals("pacs.example", node.host());
    assertEquals(11112, node.port());

    dialog.setPortText("4242");
    assertEquals(4242, dialog.apply().port());
  }

  @Test
  void dimseDialogAddsAndReplacesNodesOnListView() {
    DicomNodeListView view = new DicomNodeListView();
    assertEquals(1, view.nodes().size());
    DicomNodeDialog dialog = new DicomNodeDialog();
    dialog.setDescription("orthanc");
    dialog.setAeTitle("ORTHANC");
    dialog.setHost("127.0.0.1");
    dialog.setPortText("4242");
    DefaultDicomNode added = dialog.applyTo(view, null);
    assertEquals(2, view.nodes().size());
    assertEquals("ORTHANC@127.0.0.1:4242", added.endpoint());

    dialog.load(added);
    dialog.setPortText("11112");
    DefaultDicomNode replaced = dialog.applyTo(view, added);
    assertEquals(2, view.nodes().size());
    assertEquals(11112, replaced.port());
    view.select(view.nodes().indexOf(replaced));
    assertSame(replaced, view.selected());
  }

  @Test
  void httpHeadersEditorReplacesDuplicateNamesAndAppliesToRsQuery() {
    HttpHeadersEditor editor = new HttpHeadersEditor();
    assertFalse(editor.addHeader("  ", "x"));
    assertTrue(editor.addHeader("Accept", "application/dicom+json"));
    assertTrue(editor.addHeaderLine("Authorization: Bearer secret"));
    assertTrue(editor.addHeader("accept", "application/dicom"));
    assertEquals(2, editor.items().size());
    Map<String, String> headers = editor.headers();
    assertEquals("application/dicom", headers.get("accept"));
    assertEquals("Bearer secret", headers.get("Authorization"));

    editor.setDraft("X-Trace", "1");
    assertTrue(editor.addContent());
    assertEquals("1", editor.headers().get("X-Trace"));
    editor.select(0);
    editor.setDraft("Accept", "application/json");
    assertTrue(editor.modifyContent());
    assertEquals("application/json", editor.headers().get("Accept"));
    assertTrue(editor.removeHeader("X-Trace"));
    assertFalse(editor.headers().containsKey("X-Trace"));

    RsQueryParams params = new RsQueryParams();
    editor.applyTo(params);
    assertEquals("application/json", params.headers().get("Accept"));
    assertEquals("Bearer secret", params.headers().get("Authorization"));
  }

  @Test
  void abstractListEditorAddEditDeleteSelection() {
    HttpHeadersEditor editor = new HttpHeadersEditor();
    editor.setDraft("A", "1");
    assertTrue(editor.addContent());
    editor.setDraft("B", "2");
    assertTrue(editor.addContent());
    assertEquals(2, editor.items().size());
    editor.select(0);
    editor.setDraft("A", "9");
    assertTrue(editor.modifyContent());
    assertEquals("9", editor.headers().get("A"));
    editor.select(1);
    assertTrue(editor.deleteContent());
    assertEquals(List.of("A: 9"), editor.items());
    assertEquals("add", editor.addButton().getName());
    assertEquals("edit", editor.editButton().getName());
    assertEquals("delete", editor.deleteButton().getName());
  }

  @Test
  void authenticationPersistenceRoundTripsAndDropsBlankIds() {
    WProperties prefs = new WProperties();
    DefaultAuthMethod oidc = new DefaultAuthMethod("oidc", "Authorization", "abc");
    DefaultAuthMethod api = new DefaultAuthMethod("api-key", "X-Api-Key", "k");
    AuthenticationPersistence.save(prefs, List.of(oidc, api, new DefaultAuthMethod(" ", "H", "x")));
    assertEquals("oidc,api-key", prefs.getProperty(AuthenticationPersistence.IDS_KEY));
    List<AuthMethod> loaded = AuthenticationPersistence.load(prefs);
    assertEquals(2, loaded.size());
    assertEquals("oidc", loaded.get(0).getId());
    assertEquals(Map.of("Authorization", "Bearer abc"), loaded.get(0).authorizationHeaders());
    assertEquals(Map.of("X-Api-Key", "Bearer k"), loaded.get(1).authorizationHeaders());

    AuthenticationPersistence.upsert(prefs, new DefaultAuthMethod("oidc", "Authorization", "xyz"));
    List<AuthMethod> afterUpsert = AuthenticationPersistence.load(prefs);
    assertEquals("oidc", afterUpsert.getFirst().getId());
    assertEquals("xyz", ((DefaultAuthMethod) afterUpsert.getFirst()).token());
    assertTrue(AuthenticationPersistence.remove(prefs, "api-key"));
    assertEquals(1, AuthenticationPersistence.load(prefs).size());
    assertFalse(AuthenticationPersistence.remove(prefs, "missing"));
  }

  @Test
  void authMethodDialogPersistsAndEditorReloads() {
    AuthMethodDialog dialog = new AuthMethodDialog();
    dialog.setId("hospital");
    dialog.setHeader("");
    dialog.setToken("tok");
    DefaultAuthMethod applied = dialog.apply();
    assertEquals("hospital", applied.getId());
    assertEquals("Authorization", applied.header());
    assertEquals("tok", applied.token());

    WProperties prefs = new WProperties();
    dialog.persistTo(prefs);
    AuthenticationEditor editor = new AuthenticationEditor();
    editor.loadFrom(prefs);
    assertEquals(1, editor.items().size());
    editor.dialog().setId("second");
    editor.dialog().setHeader("X-Token");
    editor.dialog().setToken("t2");
    assertTrue(editor.addContent());
    editor.saveTo(prefs);
    List<AuthMethod> loaded = AuthenticationPersistence.load(prefs);
    assertEquals(2, loaded.size());
    editor.select(0);
    editor.dialog().load(editor.selected());
    editor.dialog().setToken("rotated");
    assertTrue(editor.modifyContent());
    assertEquals("rotated", ((DefaultAuthMethod) editor.selected()).token());
  }

  @Test
  void dicomWebDialogRejectsNonHttpAndMergesAuthOverHeaders() {
    DicomWebNodeDialog dialog = new DicomWebNodeDialog();
    dialog.setDescription("arc");
    dialog.setUrl("dcm4chee");
    assertNull(dialog.apply());
    dialog.setUrl("https://pacs.example/dcm4chee-arc/aets/DCM4CHEE/rs");
    dialog.setWebType(DicomWebNode.WebType.STOW_RS);
    dialog.headersEditor().addHeader("Accept", "application/dicom+json");
    dialog.headersEditor().addHeader("Authorization", "Bearer stale");
    DefaultAuthMethod auth = new DefaultAuthMethod("oidc", "Authorization", "fresh");
    dialog.setAuthMethod(auth);
    DicomWebNode node = dialog.apply();
    assertEquals("arc", node.description());
    assertEquals(DicomWebNode.WebType.STOW_RS, node.webType());
    assertTrue(node.dicomWeb());
    assertEquals("application/dicom+json", node.headers().get("Accept"));
    Map<String, String> request = node.requestHeaders();
    assertEquals("application/dicom+json", request.get("Accept"));
    assertEquals("Bearer fresh", request.get("Authorization"));

    dialog.setUrl("http://localhost/rs");
    dialog.setWebType(DicomWebNode.WebType.QIDO_RS);
    assertEquals(DicomWebNode.WebType.QIDO_RS, dialog.apply().webType());
    assertFalse(DicomWebNode.httpUrl(""));
    assertFalse(DicomWebNode.httpUrl("ftp://x"));
    assertTrue(DicomWebNode.httpUrl("  http://127.0.0.1/rs  "));
  }
}
