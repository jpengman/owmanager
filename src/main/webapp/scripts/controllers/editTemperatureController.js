

angular.module('oWManager').controller('EditTemperatureController', function($scope, $routeParams, $location, flash, TemperatureResource , SensorResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.temperature = new TemperatureResource(self.original);
            SensorResource.queryAll(function(items) {
                $scope.sensorSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        sensorId : item.sensorId
                    };
                    var labelObject = {
                        value : item.sensorId,
                        text : item.address
                    };
                    if($scope.temperature.sensor && item.sensorId == $scope.temperature.sensor.sensorId) {
                        $scope.sensorSelection = labelObject;
                        $scope.temperature.sensor = wrappedObject;
                        self.original.sensor = $scope.temperature.sensor;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The temperature could not be found.'});
            $location.path("/Temperatures");
        };
        TemperatureResource.get({TemperatureId:$routeParams.TemperatureId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.temperature);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The temperature was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.temperature.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/Temperatures");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The temperature was deleted.'});
            $location.path("/Temperatures");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.temperature.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("sensorSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.temperature.sensor = {};
            $scope.temperature.sensor.sensorId = selection.value;
        }
    });
    
    $scope.get();
});