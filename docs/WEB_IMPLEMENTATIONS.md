# Web Implementations

The web layer starts with local PDF selection in the browser and a PHP service boundary. Parsing/rendering libraries can be added behind explicit adapters. Browser uploads should be opt-in, bounded and validated; server processing must never assume a trusted file.