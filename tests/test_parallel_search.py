import unittest

from performance import search_pages_parallel


class TestParallelSearch(unittest.TestCase):
    def test_preserves_page_order(self):
        pages = ["alpha", "beta alpha", "gamma", "alpha delta"]
        result = search_pages_parallel(pages, "alpha", max_workers=2)
        self.assertEqual([0, 1, 3], result)

    def test_empty_query_returns_no_matches(self):
        self.assertEqual(search_pages_parallel(["alpha"], ""), [])


if __name__ == "__main__":
    unittest.main()
