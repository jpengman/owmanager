angular.module('oWManager').factory('SensorResource', function($resource){
    var resource = $resource('rest/sensors/:SensorId',{SensorId:'@sensorId'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});