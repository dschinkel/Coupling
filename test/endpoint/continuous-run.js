const webpackRunner = require('../webpackRunner');
var config = require('./webpack.config');
var childProcess = require('child_process');
const Promise = require('bluebird');

const runHelpers = require('../run-helpers');
const forkHelpers = require('./../fork-helpers');

function forkJasmine() {
  return forkHelpers.forkJasmine('test/endpoint', '.tmp', 'test.js', __dirname + '/../../../test-output', 'endpoint.xml');
}

const removeTempDirectory = function () {
  runHelpers.removeTempDirectory(__dirname + '/.tmp')
};

process.env.PORT = 3001;

const appProcess = childProcess.fork('./test/endpoint/startForkedApp');
new Promise(function (resolve, reject) {
  appProcess.on('message', function (json) {
    if (json.message === 'Application Ready') {
      resolve(json);
    } else {
      reject(json);
    }
  })
})
  .then(function () {
    var testRun = undefined;
    const watcher = webpackRunner.watch(config, function (err, stats) {
      console.log('stats', stats.toString('minimal'));
      if (!err) {
        if (testRun) {
          testRun = testRun
            .then(forkJasmine, function (err) {
              console.log('Exiting:', err);
              process.exit(-1);
            })
        } else {
          testRun = forkJasmine();
        }

      } else {
        console.log(err);
      }
    });
    appProcess.on('exit', function () {
      watcher.close();
    })
  });


process.on('SIGINT', function () {
  console.log("Caught interrupt signal");
  removeTempDirectory();
});