/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.calypso.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.service.CalypsoService;

/**
 * @author lim.han
 */
@RestController
@RequestMapping("/api")
public class ContactRestController {

    private final CalypsoService calypsoService;


    @Autowired
    public ContactRestController(CalypsoService calypsoService) {
        this.calypsoService = calypsoService;
    }

    @RequestMapping(value = "/contacts/types", method = RequestMethod.GET)
    public Collection<ContactType> populateContactTypes() {
        return this.calypsoService.findContactTypes();
    }
    
    @RequestMapping(value = "/businessPartners/{businessPartnerId}/contacts", method = RequestMethod.POST)
    public Contact addContact(@PathVariable("businessPartnerId") int businessPartnerId, @RequestBody Contact contact) {
        BusinessPartner businessPartner = this.calypsoService.findBusinessPartnerById(businessPartnerId);
        businessPartner.addContact(contact);
        this.calypsoService.saveContact(contact);
        return contact;
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public Collection<Contact> findAllContacts() {
    	return this.calypsoService.findContacts();	
    }

    @RequestMapping(value = "/contacts/search", method = RequestMethod.GET)
    public Collection<Contact> search(@RequestParam String q) {
    	return this.calypsoService.findContactByName(q);
    }
}
