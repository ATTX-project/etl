from wf_api.utils.db import connect_DB
import unittest
import pymysql as mysql


class DBTestCase(unittest.TestCase):
    """Test for DB connection."""

    def setUp(self):
        """Set up test fixtures."""

    def test_connection(self):
        """Test connection."""
        cursor = connect_DB('database.conf')
        if isinstance(cursor, mysql.cursors.DictCursor):
            self.assertRaises(Exception)
        else:
            pass
