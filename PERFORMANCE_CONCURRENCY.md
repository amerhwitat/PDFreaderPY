# Performance & Concurrency Policy

PDF processing should use bounded concurrency for independent page I/O/search/render preparation while keeping Tkinter UI updates on the main thread.

- Use a bounded `ThreadPoolExecutor` for independent PyMuPDF page searches when the workload is large enough.
- Never mutate Tkinter widgets from worker threads; return results and apply them with `after()` on the UI thread.
- Limit workers by CPU count and page count; keep a single-thread path for debugging.
- Avoid creating a new executor for every small operation.
- Preserve page order and deterministic search-result ordering.
- Benchmark real documents before changing worker counts.
