# WP-15 ViewerHub / dcm4chee IID (Have, not §6 Pass)

Pin: Weasis **4.7.3**. Sibling gateway — **not** a Felix `auto.start` bundle. Desktop Weasis stands alone.

## Have

- `GET /display` and `GET /display/IHEInvokeImageDisplay` → `weasis://` with `$weasis:config cdb=` (IID **requires** `cdb`).
- Date filters: `lowerDateTime` = studies **older than**; `upperDateTime` = **more recent than** (ViewerHub page, not typical IHE).
- dcm4chee matrix: connector **7.x** + `../../` for arc ≤5.30; **8.x** + `../../../` for 5.31+; placeholders `{}` / `IID_PATIENT_URL` / `{{patientID}}`.
- View button: `cdb` + `access_token` + `target=_self` then ViewerHub emits `weasis://`.
- Groups: user cannot belong to a host group. Package tab: `weasis-native *.zip`. Translation: `weasis-i18n-dist-*.zip`.
- Manifest cache TTL **3 minutes**. `version-compatibility.json` pin 4.7.3 → minimal 4.7.0.

## N/A (live)

- Keycloak, Redis 24 h rebuild, Imaging Hub compose, live dcm4chee UI click.

GUI: **not run**. Do not tick CHECKLIST §6 Pass.
