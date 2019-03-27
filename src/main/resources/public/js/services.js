angular.module('app.services', []).factory('Shipwreck', function($resource) {
  return $resource('/api/get-teams', { id: '@id', firstName: '@firstName', lastName: '@lastName',
   email: '@email', teamName: '@teamName', positionDescription: '@positionDescription'}, {
    update: {
      method: 'GET'
    }
  });
}).service('popupService',function($window){
    this.showPopup=function(message){
        return $window.confirm(message);
    }
});




