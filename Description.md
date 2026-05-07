# FastThumbnail (v0.1.0-Blueprint)

✅ **1. Directory Thumbnails (Win11) via JNI → absolut möglich**
Windows liefert Thumbnails über die Shell COM APIs, nicht über klassische Win32‑Funktionen.

Die relevanten Interfaces:
- `IShellItem`
- `IShellItemImageFactory` → das ist der heilige Gral
- optional: `IThumbnailCache` (Win7+)
- Legacy: `IExtractImage` (XP/7‑Style)

Mit `SHCreateItemFromParsingName()` erzeugst du ein `IShellItem`, dann:

```cpp
IShellItemImageFactory* factory;
item->QueryInterface(IID_PPV_ARGS(&factory));
factory->GetImage(size, SIIGBF_RESIZETOFIT, &hBitmap);
```

Das liefert dir exakt dieselben Thumbnails wie der Explorer, inklusive:
- Folder‑Preview (mit Miniaturbildern der Dateien)
- Video‑Thumbnails
- PDF‑Thumbnails
- Office‑Thumbnails
- High‑DPI Varianten (256×256, 512×512)

Und das Beste: Windows cached das alles, also extrem schnell.

✅ **2. Directory Icons (nicht Thumbnail)**
Wenn du nur das Icon willst (z.B. Standard‑Folder‑Icon):
- `SHGetFileInfoW`
- oder moderner: `SHGetStockIconInfo`

Aber das ist nicht das, was der Explorer zeigt — der Explorer zeigt Thumbnails, nicht Icons.

✅ **3. Zugriff auf die Win11 Folder Preview (die gelbe Mappe mit Inhalt)**
Ja, das ist Teil des Thumbnail‑Systems.
Win11 generiert Folder‑Previews dynamisch:
1. nimmt die ersten paar Dateien im Ordner
2. generiert Mini‑Thumbnails
3. legt sie in die Folder‑Preview

Das bekommst du 1:1 über `IShellItemImageFactory`.

✅ **4. JNI‑Integration in FastJava → trivial**
Wir brauchen nur:
- `CoInitializeEx`
- `SHCreateItemFromParsingName`
- `IShellItemImageFactory::GetImage`
- `GDI+` oder `CreateDIBSection` → Pixel extrahieren
- Rückgabe als `jbyteArray` oder `DirectByteBuffer`

Das ist ein 150‑Zeilen‑Modul.

✅ **5. Java‑Side Alternativen**
❌ `sun.awt.shell.ShellFolder`
- langsam
- liefert oft nur 32×32 oder 48×48 Icons
- keine echten Win11‑Thumbnails
- nicht zuverlässig

Für FastJava ungeeignet.

🔥 **6. Warum das für FastJava perfekt ist**
Du hast bereits:
- **FastFileIndex** → liefert Dateiliste
- **FastIO** → liefert ultraschnelle Metadaten
- **FastImage** → kann Bitmaps verarbeiten

**Was fehlt?**
👉 **FastThumbnail**  
Ein Modul, das:
- native Thumbnails lädt
- asynchron arbeitet
- caching unterstützt
- direkt FastImage zurückgibt

Damit kannst du in der Demo:
- links: Dateiname
- rechts: echtes Win11‑Thumbnail
- smooth scroll
- lazy loading
- 60 FPS updates

Das wäre krank gut.
