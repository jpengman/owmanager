
angular.module('oWManager').controller('NewTemperatureController', function ($scope, $location, locationParser, flash, TemperatureResource , SensorResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.temperature = $scope.temperature || {};
    
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
            $scope.temperature.sensor = {};
            $scope.temperature.sensor.sensorId = selection.value;
        }
    });
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The temperature was created successfully.'});
            $location.path('/Temperatures');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        TemperatureResource.save($scope.temperature, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Temperatures");
    };
});