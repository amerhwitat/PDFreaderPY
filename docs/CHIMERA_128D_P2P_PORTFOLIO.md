# Chimera 128D + P2P Portfolio Integration

PDFreaderPY is the document-ingestion boundary for provenance-preserving research objects.

Extracted pages, coordinates, timestamps, observer/provenance information, events and object properties may be represented through the semantic 128D profile. Optional P2P exchange shares explicit research metadata or derived records rather than silently publishing source documents.

The common envelope validates peer identity/capabilities, sequence numbers, payload hashes and optional signatures. Supported logical operations are hello, request, response, publish, snapshot, delta and acknowledgement.

No unsolicited scanning, credential/private-key exchange, arbitrary executable transfer or remote command execution is part of the integration.

Original code is GPLv3-or-later; PyMuPDF, Pillow and other dependencies retain their licenses.
