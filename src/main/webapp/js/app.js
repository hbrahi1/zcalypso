var app = angular.module('calypso', ['ui.router','ui.router.stateHelper','ngAnimate','ngCookies','ngResource','ngMockE2E','ngStorage']);


/** Start of Configurable constants **/
app.constant('useMockData', false);
app.constant('context', '/calypso');
/** End of Configurable constants **/

app.config(['stateHelperProvider','$urlRouterProvider','$urlMatcherFactoryProvider',function(stateHelperProvider,$urlRouterProvider,$urlMatcherFactoryProvider) {

	$urlRouterProvider.otherwise("/");

	$urlMatcherFactoryProvider.strictMode(false)

	stateHelperProvider.state({
		name: "landing",
		url: "/",
		templateUrl: "components/landing/landing.html",
		controller: "MainController",
		data: { requireLogin : false }
	}).state({
		name: "dashboard",
		url: "/dashboard",
		templateUrl: "components/dashboard/dashboard.html",
		controller: "DashboardController",
		data: { requireLogin : true }
	}).state({
		name: "vets",
		url: "/vets",
		templateUrl: "components/veterinarians/veterinarians.html",
		controller: "VeterinarianController",
		data: { requireLogin : true }
	}).state({
		name: "contacts",
		url: "/contacts",
		templateUrl: "components/contacts/contacts.html",
		controller: "ContactController",
		data: { requireLogin : true }
	}).state({
		name: "businessPartners",
		url: "/businessPartners",
		templateUrl: "components/businessPartners/businessPartners.html",
		controller: "BusinessPartnerController",
		data: { requireLogin : true }
	}).state({
		name: "businessPartnerDetails",
		url: "/businessPartners/:id",
		templateUrl: "components/businessPartners/businessPartner_details.html",
		controller: "BusinessPartnerDetailsController",
		data: {requireLogin : true}
	});

} ]);

/** Controllers **/
app.controller('MainController', MainController);
app.controller('DashboardController', DashboardController);
app.controller('VeterinarianController', VeterinarianController);
app.controller('ContactController', ContactController);
app.controller('ContactDetailsController', ContactDetailsController);
app.controller('BusinessPartnerController', BusinessPartnerController);
app.controller('BusinessPartnerDetailsController', BusinessPartnerDetailsController);
app.controller('AddBusinessPartnerController', AddBusinessPartnerController);
app.controller('VisitController', VisitController);
app.controller('SearchController', SearchController);

/** Services **/
app.factory('BusinessPartner', BusinessPartner);
app.factory('Contact', Contact);
app.factory('BusinessPartnerContact', BusinessPartnerContact);
app.factory('Vet', Vet);
app.factory('Visit', Visit);
app.factory('ContactType', ContactType);
app.factory('MockService', MockService);

/** Directives **/

app.directive('scrollToTarget', function() {
  return function(scope, element) {
    element.bind('click', function() {
    	angular.element('html, body').stop().animate({
			scrollTop: angular.element(angular.element(element).attr('href')).offset().top - 20
		}, 1500);
		return false;
    });
  };
});

app.directive('datePicker', DatePickerDirective);

app.run(function(useMockData, MockService) {
	MockService.mock(useMockData);
});