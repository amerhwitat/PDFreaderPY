#!/usr/bin/env python3
"""PDF page/image ingestion bridge for the Ancient Script Scanner.

Usage example:
    python ancient_script_ingest.py inscription.pdf --page 1 --output scan.json

PyMuPDF is used only for page rendering. The actual scanner remains in the
separate nlp repository so OCR/segmentation and document ingestion stay
modular and provenance can identify the originating PDF page.
"""
from __future__ import annotations

import argparse
import json
import tempfile
from pathlib import Path

import fitz


def render_page(pdf: Path, page_number: int, output: Path) -> Path:
    if page_number < 1:
        raise ValueError("page must be >= 1")
    document = fitz.open(pdf)
    try:
        if page_number > len(document):
            raise ValueError(f"page {page_number} exceeds PDF page count {len(document)}")
        page = document[page_number - 1]
        pix = page.get_pixmap(matrix=fitz.Matrix(2, 2), alpha=False)
        pix.save(output)
    finally:
        document.close()
    return output


def build_evidence_record(pdf: Path, page: int, image: Path, scan_result: dict) -> dict:
    return {
        "schema": "ancient-script-pdf-ingest/v1",
        "source_document": str(pdf),
        "source_page": page,
        "rendered_image": str(image),
        "scanner_result": scan_result,
        "provenance_note": "Rendered from the specified PDF page; retain the original PDF and publication/license metadata.",
    }


def main() -> int:
    parser = argparse.ArgumentParser(description="Render a PDF page for Ancient Script Scanner processing")
    parser.add_argument("pdf", type=Path)
    parser.add_argument("--page", type=int, default=1)
    parser.add_argument("--output", type=Path, default=Path("ancient_script_evidence.json"))
    args = parser.parse_args()

    # Keep imports lazy so PDFreaderPY can still be used independently.
    try:
        from thamudic_scanner import scan_image
    except ImportError as exc:
        raise SystemExit("Install/use the nlp scanner package or add its repository to PYTHONPATH") from exc

    with tempfile.TemporaryDirectory(prefix="ancient_script_") as tmp:
        image = Path(tmp) / f"page_{args.page}.png"
        render_page(args.pdf, args.page, image)
        result = scan_image(image)
        evidence = build_evidence_record(args.pdf, args.page, image, result)
        args.output.write_text(json.dumps(evidence, ensure_ascii=False, indent=2), encoding="utf-8")

    print(f"Created evidence record: {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
