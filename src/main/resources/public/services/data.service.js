angular.module('app')
.service('dataService', ['$http', 'configService', function($http, configService) {
    this.getColumns = function() {
        return $http.get(configService.getBaseUrl() + '/columns');
    };

    this.getAverage = function(column, offset) {
        if (!column) {
            throw new Error('column is undefined');
        }
        if (offset) {
            return $http.get(configService.getBaseUrl() + '/average/' + column + '?offset=' + offset);
        } else {
            return $http.get(configService.getBaseUrl() + '/average/' + column);
        }
    };
}]);
