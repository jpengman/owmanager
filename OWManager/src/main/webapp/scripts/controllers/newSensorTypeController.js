
angular.module('oWManager').controller('NewSensorTypeController', function ($scope, $location, locationParser, flash, SensorTypeResource ) {
    $scope.disabled = false;
    $scope.$location = $location;
    $scope.sensorType = $scope.sensorType || {};
    

    $scope.save = function() {
        var successCallback = function(data,responseHeaders){
            var id = locationParser(responseHeaders);
            flash.setMessage({'type':'success','text':'The sensorType was created successfully.'});
            $location.path('/SensorTypes');
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        SensorTypeResource.save($scope.sensorType, successCallback, errorCallback);
    };
    
    $scope.cancel = function() {
        $location.path("/SensorTypes");
    };
});