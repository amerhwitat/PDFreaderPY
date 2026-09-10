# C++ Thamudic bridge

PDFreaderPY can expose extracted PDF/image text to the shared Chimera Thamudic C++ core. The bridge is intentionally data-oriented: PDF decoding remains an input adapter, while Unicode extraction/transliteration/scanner primitives live in the shared implementation.

See the canonical implementation in `amerhwitat/nlp/cpp/thamudic` and keep interchange fixtures compatible across languages.
