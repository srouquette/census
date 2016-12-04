'use strict';

angular.module('app', [
  'ngRoute',
  'smart-table'
])
.config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {
    $routeProvider.when('/', {
        templateUrl: '/views/table.html',
        controller: 'appController'
    })
    .when('/column/:column', {
        templateUrl: '/views/table.html',
        controller: 'appController',
        noReload: true,
        reloadOnSearch: false
    })
    .otherwise({ redirectTo: '/' });
}]);
