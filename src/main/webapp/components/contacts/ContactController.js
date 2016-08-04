var ContactController = ['$scope', 'Contact', function($scope, Contact) {

	$scope.$on('$viewContentLoaded', function(event){
		$('html, body').animate({
		    scrollTop: $("#contacts").offset().top
		}, 1000);
	});

	$scope.contacts = Contact.query();

}];

var ContactDetailsController = ['$scope','ContactType','BusinessPartnerContact',function($scope,ContactType,BusinessPartnerContact,Contact) {
	$scope.contactTypes = ContactType.query();
	
	$scope.save = function(){
		currentBusinessPartnerId = $scope.currentBusinessPartner.id;

		for (i=0; i<$scope.contactTypes.length; i++){
			if ($scope.contactTypes[i].id == $scope.currentContact.type.id){
				$scope.currentContact.type.name = $scope.contactTypes[i].name;
				break;
			}
		}
		
		BusinessPartnerContact.save({businessPartnerId:currentBusinessPartnerId},$scope.currentContact,function(contact) {
			var newContact = true;
			for (i=0;i<$scope.currentBusinessPartner.contacts.length;i++) {
				if($scope.currentBusinessPartner.contacts[i].id == contact.id) {
					$scope.currentBusinessPartner.contacts[i] == contact;
					newContact = false;
					break;
				}
			}
			if(newContact) {
				$scope.currentBusinessPartner.contacts.push(contact);
			}
		});
	};
}];