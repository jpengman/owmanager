
angular.module('oWManager').controller('NewTemperaturesArchiveController', function ($scope, $location, locationParser, flash, TemperaturesArchiveResource , SensorResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.temperaturesArchive = $scope.temperaturesArchive || {};
    
    $scope.sensorList = SensorResource.queryAll(function(items){
        $scope.sensorSelectionList = $.map(items, function(item) {
            return ( {
                value : item.sensorId,
                text : item.address
            });
        });
    });
    $scope.$watch("sensorSelection", function(selection) {
        if ( typeof selection != 'undefined') {
            $scope.temperaturesArchive.sensor = {};
            $scope.temperaturesArchive.sensor.sensorId = selection.value;
        }
    });
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The temperaturesArchive was created successfully.'});
            $location.path('/TemperaturesArchives');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        TemperaturesArchiveResource.save($scope.temperaturesArchive, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/TemperaturesArchives");
    };
});