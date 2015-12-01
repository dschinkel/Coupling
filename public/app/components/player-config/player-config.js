"use strict";

angular.module("coupling.controllers")
  .controller('PlayerConfigController', ['$scope', 'Coupling', '$location', '$route',
    function ($scope, Coupling, $location, $route) {
      $scope.original = $scope.player;
      $scope.player = angular.copy($scope.player);

      $scope.savePlayer = function () {
        Coupling.savePlayer($scope.player);
        $route.reload();
      };

      $scope.removePlayer = function () {
        if (confirm("Are you sure you want to delete this player?")) {
          Coupling.removePlayer($scope.player)
            .then(function () {
              $location.path("/" + $scope.tribe._id + "/pairAssignments/current");
            });
        }
      };

      $scope.$on('$locationChangeStart', function () {
        if ($scope.playerForm.$dirty) {
          var answer = confirm("You have unsaved data. Would you like to save before you leave?");
          if (answer) {
            Coupling.savePlayer($scope.player);
          }
        }
      });
    }]);

angular.module("coupling.directives")
  .directive('playerConfig', function () {
    return {
      controller: 'PlayerConfigController',
      bindToController: true,
      restrict: 'E',
      templateUrl: '/app/components/player-config/player-config.html'
    }
  });