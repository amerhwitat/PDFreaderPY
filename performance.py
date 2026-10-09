"""Small, deterministic parallel text-search helpers for PDF pages.

The function deliberately returns page indexes in source order regardless of
worker completion order, keeping the result compatible with the sequential
reader/search contract.
"""

from concurrent.futures import ThreadPoolExecutor
from typing import Iterable, Optional


def search_pages_parallel(
    pages: Iterable[str],
    query: str,
    max_workers: Optional[int] = None,
) -> list[int]:
    """Return indexes of pages containing *query*, case-insensitively.

    An empty or whitespace-only query returns no matches. The input iterable is
    materialized once so it can be searched by multiple workers deterministically.
    """
    page_list = list(pages)
    needle = query.casefold().strip()
    if not needle:
        return []

    def matches(item: tuple[int, str]) -> Optional[int]:
        index, page_text = item
        return index if needle in page_text.casefold() else None

    with ThreadPoolExecutor(max_workers=max_workers) as pool:
        results = pool.map(matches, enumerate(page_list))
        return [index for index in results if index is not None]
