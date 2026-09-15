# Commands (Weasis 4.7 docs)

Source: [Weasis Commands](https://weasis.org/en/basics/commands/) (3.5.1+ POSIX syntax) and [Weasis Web Protocol](https://weasis.org/en/getting-started/weasis-protocol/).

Console:

```text
telnet localhost 17179
g! help
g! lb
```

At **launch**, prefix with `$`. In Gogo, no `$`. Commands containing `&` or spaces must be quoted.

`dcmview2d:*` drives an already-open 2D viewer; it is **not** for cold start.

## dcmview2d

| Command | Usage |
|---|---|
| `dcmview2d:layout` | `(-n NUMBER \| -i ID)` split-screen |
| `dcmview2d:mouseLeftAction` | `sequence\|winLevel\|zoom\|pan\|rotation\|crosshair\|measure\|draw\|contextMenu\|none` |
| `dcmview2d:move` | `-- X Y` (require `--` before negatives) |
| `dcmview2d:reset` | `-a` or `winLevel\|zoom\|pan\|rotation` |
| `dcmview2d:scroll` | `-s\|-i\|-d NUMBER` |
| `dcmview2d:synch` | `None\|Stack\|Tile` |
| `dcmview2d:wl` | `-- WIN LEVEL` |
| `dcmview2d:zoom` | set 0.0–12.0; **0** default; **−200** best fit; **−100** real size |

## dicom / image / weasis / acquire

| Command | Usage |
|---|---|
| `dicom:get` | `-l PATH` `-w URI` `-r URI` `-z URI` `-p` `-i DATA` |
| `dicom:close` | `-a` / `-p ID` (since 4.4.1) / `-y` study UID / `-s` series UID |
| `dicom:rs` | DICOMweb QIDO/WADO-RS (`-u URL -r QUERYPARAMS` plus headers) |
| `image:get` | `-f FILE` `-u URL` |
| `image:close` | `-a` / `-g` group / `-s` series |
| `weasis:info` | `-v` version / `-a` specifications |
| `weasis:ui` | `-q` quit / `-v` window on top |
| `acquire:patient` | `-x\|-i\|-s\|-u` patient context XML |
| `weasis:config` | **launch only** (`cdb`, `pro`, …) |

Dicomizer launch example from docs:

```text
$weasis:config pro="felix.extended.config.properties file:conf/dicomizer.json" pro="gosh.port 17181"
```
