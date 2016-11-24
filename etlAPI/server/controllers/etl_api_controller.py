
# @app.route('/')
def api_root():
    return 'Welcome'

# @app.route('/activity')
def activity_get(modifiedSince = None):
    return 'do some magic!'

def activity_post():
    return 'do some magic!'

# @app.route('/workflow')
def workflow_get(modifiedSince = None):
    return 'do some magic!'

def workflow_post():
    return 'do some magic!'
