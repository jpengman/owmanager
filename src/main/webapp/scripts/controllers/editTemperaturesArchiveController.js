

angular.module('oWManager').controller('EditTemperaturesArchiveController', function($scope, $routeParams, $location, flash, TemperaturesArchiveResource , SensorResource) {
    var self = this;
    $scope.disabled = false;
    $scope.$location = $location;
    
    $scope.get = function() {
        var successCallback = function(data){
            self.original = data;
            $scope.temperaturesArchive = new TemperaturesArchiveResource(self.original);
            SensorResource.queryAll(function(items) {
                $scope.sensorSelectionList = $.map(items, function(item) {
                    var wrappedObject = {
                        sensorId : item.sensorId
                    };
                    var labelObject = {
                        value : item.sensorId,
                        text : item.address
                    };
                    if($scope.temperaturesArchive.sensor && item.sensorId == $scope.temperaturesArchive.sensor.sensorId) {
                        $scope.sensorSelection = labelObject;
                        $scope.temperaturesArchive.sensor = wrappedObject;
                        self.original.sensor = $scope.temperaturesArchive.sensor;
                    }
                    return labelObject;
                });
            });
        };
        var errorCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The temperaturesArchive could not be found.'});
            $location.path("/TemperaturesArchives");
        };
        TemperaturesArchiveResource.get({TemperaturesArchiveId:$routeParams.TemperaturesArchiveId}, successCallback, errorCallback);
    };

    $scope.isClean = function() {
        return angular.equals(self.original, $scope.temperaturesArchive);
    };

    $scope.save = function() {
        var successCallback = function(){
            flash.setMessage({'type':'success','text':'The temperaturesArchive was updated successfully.'}, true);
            $scope.get();
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        };
        $scope.temperaturesArchive.$update(successCallback, errorCallback);
    };

    $scope.cancel = function() {
        $location.path("/TemperaturesArchives");
    };

    $scope.remove = function() {
        var successCallback = function() {
            flash.setMessage({'type': 'error', 'text': 'The temperaturesArchive was deleted.'});
            $location.path("/TemperaturesArchives");
        };
        var errorCallback = function(response) {
            if(response && response.data && response.data.message) {
                flash.setMessage({'type': 'error', 'text': response.data.message}, true);
            } else {
                flash.setMessage({'type': 'error', 'text': 'Something broke. Retry, or cancel and start afresh.'}, true);
            }
        }; 
        $scope.temperaturesArchive.$remove(successCallback, errorCallback);
    };
    
    $scope.$watch("sensorSelection", function(selection) {
        if (typeof selection != 'undefined') {
            $scope.temperaturesArchive.sensor = {};
            $scope.temperaturesArchive.sensor.sensorId = selection.value;
        }
    });
    
    $scope.get();
});