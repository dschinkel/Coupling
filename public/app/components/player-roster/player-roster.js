"use strict";

angular.module("coupling.directives")
  .directive('playerRoster', function () {
    return {
      scope: {
        tribe: '=',
        players: '=',
        label: '=?'
      },
      restrict: 'E',
      templateUrl: '/app/components/player-roster/player-roster.html'
    }
  });