

angular.module('oWManager').controller('EditSensorController', function($scope, $routeParams, $location, flash, SensorResource , SensorTypeResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.sensor = new SensorResource(self.original);
            SensorTypeResource.queryAll(function(items) {
                $scope.sensorTypeSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        sensorTypeId : item.sensorTypeId
                    };
                    var labelObject = {
                        value : item.sensorTypeId,
                        text : item.description
                    };
                    if($scope.sensor.sensorType && item.sensorTypeId == $scope.sensor.sensorType.sensorTypeId) {
                        $scope.sensorTypeSelection = labelObject;
                        $scope.sensor.sensorType = wrappedObject;
                        self.original.sensorType = $scope.sensor.sensorType;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The sensor could not be found.'});
            $location.path("/Sensors");
        };
        SensorResource.get({SensorId:$routeParams.SensorId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.sensor);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The sensor was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.sensor.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Sensors");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The sensor was deleted.'});
            $location.path("/Sensors");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.sensor.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("sensorTypeSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.sensor.sensorType = {};
            $scope.sensor.sensorType.sensorTypeId = selection.value;
        }
    });
    
    $scope.get();
});