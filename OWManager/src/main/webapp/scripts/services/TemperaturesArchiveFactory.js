angular.module('oWManager').factory('TemperaturesArchiveResource', function($resource){
    var resource = $resource('rest/temperaturesarchives/:TemperaturesArchiveId',{TemperaturesArchiveId:'@temperatureId'},{'queryAll':{method:'GET',isArray:true},'query':{method:'GET',isArray:false},'update':{method:'PUT'}});
    return resource;
});