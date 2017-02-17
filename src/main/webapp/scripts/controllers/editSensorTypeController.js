

angular.module('oWManager').controller('EditSensorTypeController', function($scope, $routeParams, $location, flash, SensorTypeResource ) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.sensorType = new SensorTypeResource(self.original);
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The sensorType could not be found.'});
            $location.path("/SensorTypes");
        };
        SensorTypeResource.get({SensorTypeId:$routeParams.SensorTypeId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.sensorType);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The sensorType was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.sensorType.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/SensorTypes");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The sensorType was deleted.'});
            $location.path("/SensorTypes");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.sensorType.$remove(successCallback, errorCallback);
    };
    
    
    $scope.get();
});