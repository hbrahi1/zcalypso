var PetController = ['$scope', 'Pet', function($scope, Pet) {

	$scope.$on('$viewContentLoaded', function(event){
		$('html, body').animate({
		    scrollTop: $("#pets").offset().top
		}, 1000);
	});

	$scope.pets = Pet.query();

}];

var PetDetailsController = ['$scope','PetType','BusinessPartnerPet',function($scope,PetType,BusinessPartnerPet,Pet) {
	$scope.petTypes = PetType.query();
	
	$scope.save = function(){
		currentBusinessPartnerId = $scope.currentBusinessPartner.id;

		for (i=0; i<$scope.petTypes.length; i++){
			if ($scope.petTypes[i].id == $scope.currentPet.type.id){
				$scope.currentPet.type.name = $scope.petTypes[i].name;
				break;
			}
		}
		
		BusinessPartnerPet.save({businessPartnerId:currentBusinessPartnerId},$scope.currentPet,function(pet) {
			var newPet = true;
			for (i=0;i<$scope.currentBusinessPartner.pets.length;i++) {
				if($scope.currentBusinessPartner.pets[i].id == pet.id) {
					$scope.currentBusinessPartner.pets[i] == pet;
					newPet = false;
					break;
				}
			}
			if(newPet) {
				$scope.currentBusinessPartner.pets.push(pet);
			}
		});
	};
}];