#!/usr/bin/env python

import connexion

if __name__ == '__main__':
    app = connexion.App(__name__, specification_dir='./swagger/')
    app.add_api('swagger.yaml', arguments={'title': 'ATTX ETL internal API for communication between ETL tool and Graph Manager component.'})
    app.run(port=4301, server='gevent')
