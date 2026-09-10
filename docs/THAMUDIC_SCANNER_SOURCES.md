# Thamudic Scanner Sources for PDF/Document Processing

This repository can supply document ingestion, page rendering and extraction to the Thamudic/Ancient North Arabian research workflow.

## Primary scholarly source

OCIANA (Online Corpus of the Inscriptions of Ancient North Arabia): https://ociana.osu.edu/scripts_thamudic

OCIANA records that “Thamudic” is a broad historical label rather than a single uniform alphabet and recommends corpus-based identification. The scanner should therefore preserve script variety, inscription identifier, provenance and scholarly reading.

## Unicode

Unicode Old North Arabian chart: https://www.unicode.org/charts/nameslist/n_10A80.html
Unicode Core Specification: https://www.unicode.org/versions/Unicode16.0.0/core-spec/chapter-10/

The Old North Arabian block is U+10A80–U+10A9F. PDF processing must preserve these supplementary-plane characters as UTF-8/Unicode rather than replacing them with ASCII approximations.

## User-supplied online projects

- https://thamudicscan.bubbleapps.io/version-test
- https://thamudicscan-s3wz30.public.builtwithrocket.new/
- https://thamudic-scanner.softr.app/

These are external web references only; source code is not assumed to be open-source.

## Related open-source OCR references

- CuReD: https://github.com/DigitalPasts/CuReD
- Coptic Scriptorium OCR: https://github.com/CopticScriptorium/OCR
- MAS historical Arabic OCR benchmark: https://github.com/ai-forever/MAS
- Experimental hieroglyphic OCR: https://github.com/nederhof/hocr

## PDF pipeline

`PDF/image -> page extraction -> image normalization -> inscription crop -> glyph segmentation -> JSON/CSV evidence -> optional recognition -> scholarly review`.

No Tesseract or camel_tools dependency is required by the baseline Thamudic scanner. PDFreaderPY remains focused on document ingestion; recognition belongs in the scanner/NLP layer.
