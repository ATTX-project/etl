import connexion
import logging

logger = logging.getLogger('simple_example')
logger.setLevel(logging.DEBUG)

formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')

version = "0.1"

app = connexion.App(__name__, specification_dir='./swagger/')
app.add_api('swagger.yaml', arguments={'title': 'ATTX wf internal API for communication between WF Management component and Graph Manager component.'}, base_path='/v{}'.format(version))

if __name__ == '__main__':
    app.run(port=4301, host="0.0.0.0")
