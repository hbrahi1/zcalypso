var BusinessPartner = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/businessPartners/:id');
}];

var BusinessPartnerContact = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/businessPartners/:businessPartnerId/contacts', {businessPartnerId : '@businessPartnerId'});
}];

var Contact = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/contacts/:id');
}];

var Vet = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/vets/:vetId');
}];

var Visit = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/contacts/:contactId/visits', {contactId : '@id'});
}];

var ContactType = ['$resource','context', function($resource, context) {
	return $resource(context + '/api/contacts/types');
}];

var MockService = ['$httpBackend', '$http', '$q', 'context', function($httpBackend, $http, $q, context) {
	return {
		mock : function(useMockData) {
			
			var passThroughRegex = new RegExp('/static/mock-data/|components/');
			$httpBackend.whenGET(passThroughRegex).passThrough();	
			
			if(useMockData) {
				$q.defer();
				$q.all([
				        $http.get(context + '/static/mock-data/contacts.json'),
				        $http.get(context + '/static/mock-data/vets.json'),
				        $http.get(context + '/static/mock-data/businessPartners.json'),
				        $http.get(context + '/static/mock-data/businessPartner_one.json'),
				        $http.get(context + '/static/mock-data/contacttypes.json'),
				]).then(function(data) {
					console.log("Mocking /api/contacts");
					$httpBackend.whenGET(context + '/api/contacts').respond(data[0].data);
					console.log("Mocking /api/vets");
					$httpBackend.whenGET(context + '/api/vets').respond(data[1].data);
					console.log("Mocking /api/businessPartners");
					$httpBackend.whenGET(context + '/api/businessPartners').respond(data[2].data);
					console.log("Mocking /api/businessPartners/1");
					$httpBackend.whenGET(context + '/api/businessPartners/1').respond(data[3].data);
					console.log("Mocking /api/contacts/types");
					$httpBackend.whenGET(context + '/api/contacts/types').respond(data[4].data);
					
					console.log("Setting up passthrough for other urls");
					var passThroughRegex = new RegExp('/');
					$httpBackend.whenGET(passThroughRegex).passThrough();
				});
			} else {
				console.log("Setting up passthrough for other urls");
				var passThroughRegex = new RegExp('/');
				$httpBackend.whenGET(passThroughRegex).passThrough();			
				$httpBackend.whenPOST(passThroughRegex).passThrough();
				$httpBackend.whenPUT(passThroughRegex).passThrough();
				$httpBackend.whenDELETE(passThroughRegex).passThrough();
			}
		}
	}	
}];