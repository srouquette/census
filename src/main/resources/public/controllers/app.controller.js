angular.module('app')
.controller('appController', ['$scope', '$routeParams', '$route', '$location', 'configService', 'dataService',
function($scope, $routeParams, $route, $location, configService, dataService) {

    $scope.columns = null;
    $scope.selectedColumn = null;
    $scope.dataColumn = null;
    $scope.data = [];
    $scope.displayedData = [];
    $scope.moreAvailable = false;

    var offset = 0;

    // don't reload the controller when updating the route
    var original = $location.path;
    $location.path = function (path, reload) {
        if (reload === false) {
            var lastRoute = $route.current;
            var un = $scope.$on('$locationChangeSuccess', function () {
                $route.current = lastRoute;
                un();
            });
        }
        return original.apply($location, [path]);
    };

    var canUpdateData = function(column, data) {
        return column === $scope.dataColumn && data && data.length > 0;
    };

    var updateData = function(column, data) {
        offset += data.length;
        $scope.data = $scope.data.concat(data);
        $scope.displayedData = [].concat($scope.data);
        $scope.moreAvailable = data.length >= configService.getNbEntries();
    };

    dataService.getColumns()
        .then(function(res) {
            $scope.columns = res.data;
            $scope.setSelectedColumn($routeParams.column || $scope.columns[0]);
        }, function(err) {
            console.error(err);
        });

    $scope.setSelectedColumn = function(column) {
        if (!$scope.columns) {
            return;
        }
        if ($scope.columns.indexOf(column) < 0) {
            column = $scope.columns[0];
        }
        if (column === $scope.dataColumn) {
            return;
        }
        $scope.selectedColumn = column;
        $scope.dataColumn = column;
        $location.path('/column/' + column, false);
        dataService.getAverage(column)
            .then(function(res) {
                if (canUpdateData(column, res.data)) {
                    offset = 0;
                    $scope.data = [];
                    updateData(column, res.data);
                }
            }, function(err) {
                console.error(err);
            });
    }

    $scope.downloadMore = function() {
        if (!$scope.moreAvailable) {
            return;
        }
        var column = $scope.selectedColumn;
        $scope.dataColumn = column;
        dataService.getAverage(column, offset)
            .then(function(res) {
                if (canUpdateData(column, res.data)) {
                    updateData(column, res.data);
                }
            }, function(err) {
                console.error(err);
            });
    };

    $scope.getAverageColumnName = function() {
        return configService.getColumnToAverage();
    };
}]);
