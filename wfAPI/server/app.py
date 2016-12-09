import connexion
import logging
import logging.config

logging.config.fileConfig('logging.conf')
logger = logging.getLogger('mainLogger')

version = "0.1"

wfm_app = connexion.App(__name__, specification_dir='./swagger/')
wfm_app.add_api(
    'swagger.yaml',
    arguments={
        'title': 'ATTX wf internal API for communication between\
         WF Management component and Graph Manager component.'},
    base_path='/v{}'.format(version))

if __name__ == '__main__':
    wfm_app.run(port=4301, host="0.0.0.0")
