# PDFreaderPY

Python PDF reader and document-ingestion utilities.

## Ancient Script Scanner integration

`ancient_script_ingest.py` renders a selected PDF page to an image and creates a provenance-preserving evidence record for the Ancient Script Scanner maintained in `amerhwitat/nlp`.

Example:

```bash
python ancient_script_ingest.py inscription.pdf --page 1 --output scan.json
```

The originating PDF and page number remain part of the evidence record. Recognition and translation are handled by the scanner/research workflow and must remain human-reviewed.

## Dependencies

Windows:

```bash
pip install PyMuPDF Pillow
```

Linux:

```bash
pip3 install PyMuPDF Pillow
```
