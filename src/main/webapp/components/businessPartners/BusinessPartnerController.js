var BusinessPartnerController = ['$scope','$state','BusinessPartner',function($scope,$state,BusinessPartner) {
	$scope.$on('$viewContentLoaded', function(event){
		$('html, body').animate({
		    scrollTop: $("#businessPartners").offset().top
		}, 1000);
	});

	$scope.businessPartners = BusinessPartner.query();
}];

var BusinessPartnerDetailsController = ['$scope','$rootScope','$stateParams','BusinessPartner', function($scope,$rootScope,$stateParams,BusinessPartner) {

	var currentId = $stateParams.id;
	var nextId = parseInt($stateParams.id) + 1;
	var prevId = parseInt($stateParams.id) - 1;

	$scope.prevBusinessPartner = BusinessPartner.get({id:prevId});
	$scope.nextBusinessPartner = BusinessPartner.get({id:nextId});
	$scope.currentBusinessPartner = BusinessPartner.get($stateParams);

	$scope.saveBusinessPartner = function(){
		businessPartner = $scope.currentBusinessPartner;
		BusinessPartner.save(businessPartner);
	}
	
	$scope.addPet = function() {
		$scope.petFormHeader = "Add a new Pet";
		$scope.currentPet = {type:{}};
	}
	
	$scope.editPet = function(id) {
		$scope.petFormHeader = "Edit Pet";
		for(i = 0;i < $scope.currentBusinessPartner.pets.length; i++) {
			if($scope.currentBusinessPartner.pets[i].id == id) {
				$scope.currentPet = $scope.currentBusinessPartner.pets[i];
				break;
			}
		}
	};

}];

var AddBusinessPartnerController = ['$scope','BusinessPartner', function($scope,BusinessPartner) {

	$scope.businessPartner={id:0,pets:[]};

	$scope.addBusinessPartner = function(){
		BusinessPartner.save($scope.businessPartner);
	}
}];