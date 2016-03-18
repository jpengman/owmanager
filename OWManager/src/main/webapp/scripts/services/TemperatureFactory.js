angular.module('oWManager').factory('TemperatureResource', function($resource){
    var resource = $resource('rest/temperatures/:TemperatureId',{TemperatureId:'@temperatureId'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});