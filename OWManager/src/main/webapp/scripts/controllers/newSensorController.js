
angular.module('oWManager').controller('NewSensorController', function ($scope, $location, locationParser, flash, SensorResource , SensorTypeResource) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.sensor = $scope.sensor || {};
    
    $scope.sensorTypeList = SensorTypeResource.queryAll(function(items){
        $scope.sensorTypeSelectionList = $.map(items, function(item) {
            return ( {
                value : item.sensorTypeId,
                text : item.description
            });
        });
    });
    $scope.$watch("sensorTypeSelection", function(selection) {
        if ( typeof selection != 'undefined') {
            $scope.sensor.sensorType = {};
            $scope.sensor.sensorType.sensorTypeId = selection.value;
        }
    });
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The sensor was created successfully.'});
            $location.path('/Sensors');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        SensorResource.save($scope.sensor, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/Sensors");
    };
});