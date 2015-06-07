"use strict";
var services = angular.module("coupling.services", []);

services.service("Coupling", ['$http', function ($http) {
  var Coupling = this;

  function errorMessage(url, data, statusCode) {
    return "There was a problem with request " + url + "\n" +
      "Data: <" + data + ">\n" +
      "Status: " + statusCode;
  }

  var makeErrorHandler = function (url) {
    return function (data, statusCode) {
      var message = errorMessage(url, data, statusCode);
      console.error('ALERT!\n' + message);
    }
  };

  var requestTribes = function () {
    return new RSVP.Promise(function (resolve, reject) {
      var url = '/api/tribes';
      $http.get(url).success(function (tribes) {
        resolve(tribes);
      }).error(function (data, statusCode) {
        reject(errorMessage('GET ' + url, data, statusCode));
      });
    });
  };

  var requestHistory = function (tribeId, callback) {
    var url = '/api/' + tribeId + '/history';
    $http.get(url).success(function (history) {
      Coupling.data.history = history;
      if (callback) {
        callback(history);
      }
    }).error(makeErrorHandler('GET ' + url));
  };

  this.requestSpecificTribe = function (tribeId) {
    return Coupling.getTribes().then(function (tribes) {
      var found = _.findWhere(tribes, {
        _id: tribeId
      });
      Coupling.data.selectedTribe = found;
      return new RSVP.Promise(function (resolve, reject) {
        if (found) {
          resolve(found);
        } else {
          reject("Tribe not found")
        }
      });
    })
  };

  var isInLastSetOfPairs = function (player, history) {
    var result = _.find(history[0].pairs, function (pairset) {
      if (_.findWhere(pairset, {
          _id: player._id
        })) {
        return true;
      }
    });
    return !!result;
  };

  this.requestPlayersPromise = function (tribeId, historyPromise) {
    return RSVP.hash({
      players: new RSVP.Promise(function (resolve, reject) {
        var url = '/api/' + tribeId + '/players';
        $http.get(url).success(function (players) {
          resolve(players);
        })
          .error(function (data, statusCode) {
            var message = errorMessage(url, data, statusCode);
            console.error('ALERT!\n' + message);
            reject(message);
          });
      }),
      history: historyPromise
    }).then(function (data) {
      var players = data.players;
      var history = data.history;
      _.each(players, function (player) {
        if (history.length == 0) {
          player.isAvailable = true;
        } else {
          player.isAvailable = isInLastSetOfPairs(player, history);
        }
      });
      _.each(Coupling.data.players, function (originalPlayer) {
        var newPlayer = _.findWhere(players, {
          _id: originalPlayer._id
        });
        if (newPlayer) {
          newPlayer.isAvailable = originalPlayer.isAvailable;
        }
      });
      Coupling.data.players = players;
      return players;
    })
  };

  this.requestHistoryPromise = function (tribeId) {
    return new RSVP.Promise(function (resolve) {
      requestHistory(tribeId, function (history) {
        Coupling.data.history = history;
        resolve(history);
      });
    })
  };

  var post = function (url, player, callback) {
    var postPromise = $http.post(url, player);
    if (callback) {
      postPromise.success(callback);
    }
    return postPromise.error(makeErrorHandler('POST ' + url));
  };

  var httpDelete = function (url, callback) {
    var postPromise = $http.delete(url);
    if (callback) {
      postPromise.success(callback);
    }
    postPromise.error(makeErrorHandler(url));
  };

  this.spin = function (players, tribeId) {
    var url = '/api/' + tribeId + '/spin';
    var postPromise = $http.post(url, players);
    postPromise.error(makeErrorHandler('POST ' + url));
    return postPromise.then(function (result) {
      return result.data;
    });
  };

  this.saveCurrentPairAssignments = function (tribeId, pairAssignments) {
    var url = '/api/' + tribeId + '/history';
    var postPromise = $http.post(url, pairAssignments);
    postPromise.error(makeErrorHandler('POST ' + url));
    return postPromise.then(function (result) {
      return result.data;
    });
  };

  this.savePlayer = function (player, callback) {
    post('/api/' + Coupling.data.selectedTribeId + '/players', player, callback);
    (function (tribeId, callback) {
      var url = '/api/' + tribeId + '/players';
      $http.get(url).success(function (players) {
        Coupling.data.players = players;
        if (callback) {
          callback(players);
        }
      }).error(makeErrorHandler('GET ' + url));
    })(Coupling.data.selectedTribeId);
  };

  this.removePlayer = function (player, callback) {
    httpDelete('/api/' + Coupling.data.selectedTribeId + '/players/' + player._id, callback);
    (function (tribeId, callback) {
      var url = '/api/' + tribeId + '/players';
      $http.get(url).success(function (players) {
        Coupling.data.players = players;
        if (callback) {
          callback(players);
        }
      }).error(makeErrorHandler('GET ' + url));
    })(Coupling.data.selectedTribeId);
  };

  this.selectTribe = function (tribeId) {
    if (tribeId == null) {
      return new RSVP.Promise(function (resolve) {
        Coupling.data.players = null;
        Coupling.data.currentPairAssignments = null;
        Coupling.data.history = null;
        resolve({
          selectedTribe: null,
          players: null,
          history: null
        });
      });
    } else {
      Coupling.data.selectedTribeId = tribeId;
      var historyPromise = Coupling.requestHistoryPromise(tribeId);
      return RSVP.hash({
        selectedTribe: Coupling.requestSpecificTribe(tribeId),
        players: Coupling.requestPlayersPromise(tribeId, historyPromise),
        history: historyPromise
      });
    }
  };

  this.getTribes = function () {
    return requestTribes();
  };

  this.saveTribe = function (tribe) {
    return post('/api/tribes', tribe);
  };

  this.promisePins = function (tribeId) {
    return new RSVP.Promise(function (resolve, reject) {
      var url = '/api/' + tribeId + '/pins';
      $http.get(url)
        .error(function (data, status) {
          reject(errorMessage('GET ' + url, data, status));
        })
        .then(function (response) {
          return resolve(response.data);
        });
    });
  };

  Coupling.data = {
    players: null,
    history: null
  };
}]);

services.service('randomizer', function () {
  this.next = function (maxValue) {
    var floatValue = Math.random() * maxValue;
    return Math.round(floatValue);
  }
});