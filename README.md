# PDFreaderPY

Python PDF reader and document-ingestion utilities.

## Ancient Script + Historical Object integration

- `ancient_script_ingest.py` renders a selected PDF page into an evidence image for the Ancient Script Scanner.
- `ancient_object_catalog_bridge.py` renders a page and creates a provenance-preserving object record compatible with `amerhwitat/nlp/ancient_objects_db.py`.

Example:

```bash
python ancient_script_ingest.py inscription.pdf --page 1 --output scan.json
python ancient_object_catalog_bridge.py artifact.pdf --page 2 --period iron_age --output object.json
```

The originating PDF, page number, local image path and source metadata remain part of the record. Recognition, transliteration and translation remain human-reviewed research fields.

## Dependencies

```bash
pip install PyMuPDF Pillow
```
