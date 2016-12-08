from uv.parse_config import parse_metadata_config
import unittest2 as unittest


class ParsingConfigTests(unittest.TestCase):
    """Test the Parsing of UV specific plugin encoding."""

    def setUp(self):
        """Set up."""
        pass

    def parse_metadata(self):
        """Test if input output encoding is pefromed correctly."""
        parse_metadata_config()
        return True


if __name__ == '__main__':
    unittest.main()
