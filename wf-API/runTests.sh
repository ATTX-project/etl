mkdir build/test-report
source activate
py.test tests --html=build/test-report/index.html --self-contained-html
