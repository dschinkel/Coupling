var util = require('util');
var userEmail = 'protractor@test.goo';
var monk = require("monk");
var config = require("../../config");
var usersCollection = monk(config.mongoUrl).get('users');

function authorizeUserForTribes(authorizedTribes) {
  return usersCollection.update({
    email: userEmail
  }, {
    $set: {
      tribes: authorizedTribes
    }
  }).then(function (updateCount) {
    if (updateCount == 0) {
      return usersCollection.insert({
        email: userEmail,
        tribes: authorizedTribes
      });
    }
  });
}

var helper = {
  userEmail: userEmail,
  authorizeUserForTribes: authorizeUserForTribes,
  afterEachAssertLogsAreEmpty: function () {

    afterEach(function (done) {
      browser.manage().logs().get('browser').then(function (browserLog) {
        expect(browserLog).toEqual([]);
        if (browserLog.length > 0) {
          console.log('log: ' + util.inspect(browserLog));
        }
        done();
      }, done);
      browser.waitForAngular();
    });
  }
};

module.exports = helper;