# PDFreaderPY

Python PDF reader and document-ingestion utilities.

## Source-code citation index

The executable/reference source is explicitly cited below:

- [PDF/document ingestion](ancient_script_ingest.py)
- [Ancient object catalog bridge](ancient_object_catalog_bridge.py)
- [PDF/document processing modules](.)
- [Chimera P2P contract](chimera/p2p_protocol.json)
- [Centralized Apple Objective-C + Flutter implementation](https://github.com/amerhwitat/general/tree/master/Apple-Implementations/PDFreaderPY)

## Centralized Apple Objective-C + Flutter implementation

The Apple companion is maintained in [`general/Apple-Implementations/PDFreaderPY`](https://github.com/amerhwitat/general/tree/master/Apple-Implementations/PDFreaderPY). It provides an Objective-C/Xcode native shell and Flutter iOS/macOS UI. Python/PyMuPDF processing remains behind a native/service boundary instead of being incorrectly treated as automatically embedded in an IPA.

## Ancient Script + Historical Object integration

- `ancient_script_ingest.py` renders a selected PDF page into an evidence image for the Ancient Script Scanner.
- `ancient_object_catalog_bridge.py` renders a page and creates a provenance-preserving object record compatible with `amerhwitat/nlp/ancient_objects_db.py`.
- The Thamudic platform is Python-first: PDFreaderPY remains the ingestion boundary while the shared SQLite database is consumed by the Tkinter desktop and Flask web application in `amerhwitat/nlp`.

## Apple applications

On macOS, install Xcode/XcodeGen, generate the native project, run the Flutter application, then archive/export through Xcode. Original document bytes and provenance remain local unless explicitly shared.

## Chimera 128D + P2P integration

Ingested documents and extracted evidence may be represented as Chimera multidimensional objects with geometry/page coordinates, time/provenance, observer perspective, events, object properties and extensible vector state. `chimera/p2p_protocol.json` provides the common authenticated peer-to-peer envelope for optional research-node synchronization.

## Dependencies

```bash
pip install PyMuPDF Pillow
```

## License

Original project code is released under the GNU General Public License v3 or later. Third-party dependencies retain their own licenses.
