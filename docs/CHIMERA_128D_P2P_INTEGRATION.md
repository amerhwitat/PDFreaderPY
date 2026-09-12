# Chimera 128D + authenticated P2P integration

PDFreaderPY remains a local-first document ingestion boundary while participating in the common Chimera multidimensional data contract.

Document, page, image, OCR and provenance records may be represented as 128D-aware objects: source geometry, time, observer/perspective, material/light metadata when applicable, events, object properties and interaction relationships, plus extensible perception/cognition metadata.

The optional P2P layer synchronizes provenance-preserving records through authenticated peers using request/response, pub/sub, snapshots/deltas and content-addressed objects. Peer synchronization never replaces the original evidence and must preserve source URI/path, page and provenance metadata.

Python is the reference implementation here; compatible Node.js, Java, C++ and other implementations should use the same schema. P2P is disabled by default and must not transmit credentials or arbitrary executable content.
