var SearchController = ['$scope','$http','$timeout','context', function($scope,$http,$timeout,context) {
	$scope.results = [];
	$scope.searchText = '';
    $scope.$watch('searchText', function (val) {
    	if(val != '') {
	        $http.get(context + '/api/contacts/search', { params : { q : val } }).then(function(response) {
	        	$scope.results = response.data;
	        })
    	} else {
    		$scope.results = [];
    	}
    })
}];