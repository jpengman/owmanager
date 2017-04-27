'use strict';

angular.module('oWManager',['ngRoute','ngResource'])
  .config(['$routeProvider', function($routeProvider) {
    $routeProvider
      .when('/',{templateUrl:'views/landing.html',controller:'LandingPageController'})
      .when('/Sensors',{templateUrl:'views/Sensor/search.html',controller:'SearchSensorController'})
      .when('/Sensors/new',{templateUrl:'views/Sensor/detail.html',controller:'NewSensorController'})
      .when('/Sensors/edit/:SensorId',{templateUrl:'views/Sensor/detail.html',controller:'EditSensorController'})
      .when('/SensorTypes',{templateUrl:'views/SensorType/search.html',controller:'SearchSensorTypeController'})
      .when('/SensorTypes/new',{templateUrl:'views/SensorType/detail.html',controller:'NewSensorTypeController'})
      .when('/SensorTypes/edit/:SensorTypeId',{templateUrl:'views/SensorType/detail.html',controller:'EditSensorTypeController'})
      .when('/Temperatures',{templateUrl:'views/Temperature/search.html',controller:'SearchTemperatureController'})
      .when('/Temperatures/new',{templateUrl:'views/Temperature/detail.html',controller:'NewTemperatureController'})
      .when('/Temperatures/edit/:TemperatureId',{templateUrl:'views/Temperature/detail.html',controller:'EditTemperatureController'})
      .when('/TemperaturesArchives',{templateUrl:'views/TemperaturesArchive/search.html',controller:'SearchTemperaturesArchiveController'})
      .when('/TemperaturesArchives/new',{templateUrl:'views/TemperaturesArchive/detail.html',controller:'NewTemperaturesArchiveController'})
      .when('/TemperaturesArchives/edit/:TemperaturesArchiveId',{templateUrl:'views/TemperaturesArchive/detail.html',controller:'EditTemperaturesArchiveController'})
      .when('/Graphs',{templateUrl:'views/Graph/graph.html',controller:'GraphController'})
      .otherwise({
        redirectTo: '/'
      });
  }])
  .controller('LandingPageController', function LandingPageController() {
  })
  .controller('NavController', function NavController($scope, $location) {
    $scope.matchesRoute = function(route) {
        var path = $location.path();
        return (path === ("/" + route) || path.indexOf("/" + route + "/") == 0);
    };
  });
