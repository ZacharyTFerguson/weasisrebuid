# WP-13 `weasis://` protocol (Have, not §6 Pass)

Commands are URL-encoded after `weasis://?`. The scheme opens the **already installed** desktop app.

Enterprise Chrome/Edge/Firefox policy should allow-list `weasis://*`.

Linux ships `weasis-distributions/linux/weasis.desktop` (`x-scheme-handler/weasis`).

`$weasis:config` is launch-only (`cdb`, `arg`, `pro`, `auth`, `wcfg`). `cdb` without a value uses the natively installed build.

`$dicom:get -l` local recursive; `$dicom:get -w` XML/JSON manifest; `$dicom:rs` **requires** `-u URL`.
