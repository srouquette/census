angular.module('app')
.service('configService', ['$http', function($http) {
    var nbEntries = 100;
    var columnToAverage = 'age';

    this.getBaseUrl = function() {
        return '/census';
    };

    this.getNbEntries = function() {
        return nbEntries;
    };

    this.getColumnToAverage = function() {
        return columnToAverage;
    };

    $http.get(this.getBaseUrl() + '/config')
        .then(function(res) {
            nbEntries = res.data.nbEntries;
            columnToAverage = res.data.columnToAverage;
        }, function(err) {
            console.error(err);
        });
}]);
