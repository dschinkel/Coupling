const webpackRunner = require('../webpackRunner');
const config = require('./webpack.config');
const runHelpers = require('..//run-helpers');
const childProcess = require('child_process');

const Bluebird = require('bluebird');

const startJasmine = function () {
  return runHelpers.startJasmine('test/endpoint', '.tmp', 'test.js', __dirname + '/../../build/test-results/endpoint', 'endpoint.xml')
};

const removeTempDirectory = function () {
  return runHelpers.removeTempDirectory(__dirname + '/.tmp');
};

let servlessProcess;

function waitForStart(childProcess) {
  return new Bluebird(function (resolve) {
    childProcess.stdout.addListener('data', function (data) {
      if (data.toString().includes("listening on http://localhost:3001")) {
        resolve()
      }
    });
  }).timeout(20000);
}

webpackRunner.run(config)
  .then(function () {
    process.env.PORT = "3001";
    servlessProcess = childProcess.spawn("serverless offline --port 3001", {stdio: 'pipe', shell: true});
    return waitForStart(servlessProcess)
  })
  .then(startJasmine)
  .finally(removeTempDirectory)
  .then(function () {
    servlessProcess.once('exit', function() {
      process.exit(0);
    });
    servlessProcess.kill();
  }, function (err) {
    console.error(err);
    servlessProcess.once('exit', function() {
      process.exit(1);
    });
    servlessProcess.kill();

  });