import pymysql as mysql
from ConfigParser import SafeConfigParser
from wf_api.utils.logs import app_logger


def connect_DB(db_conf=None):
    """Connect to DB by parsing configuration."""
    parser = SafeConfigParser()

    if db_conf is None:
        parser.read('database.conf')
    else:
        parser.read(db_conf)

    try:
        conn = mysql.connect(
            host=parser.get('uv_database', 'host'),
            port=3306,
            user=parser.get('uv_database', 'user'),
            passwd=parser.get('uv_database', 'passwd'),
            db=parser.get('uv_database', 'db'),
            charset='utf8')
        app_logger.info('Connecting to database.')
        # Default curosr is DictCursor
        cursor = conn.cursor(mysql.cursors.DictCursor)
        return cursor
    except Exception as error:
        app_logger.error('Connection Failed!\
            \nError Code is {0};\
            \nError Content is {1};'.format(error.args[0], error.args[1]))
        return error
