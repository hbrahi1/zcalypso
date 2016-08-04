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
package com.calypso.web;

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.service.CalypsoService;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
@SessionAttributes("contact")
public class ContactController {

    private final CalypsoService calypsoService;


    @Autowired
    public ContactController(CalypsoService calypsoService) {
        this.calypsoService = calypsoService;
    }

    @ModelAttribute("types")
    public Collection<ContactType> populateContactTypes() {
        return this.calypsoService.findContactTypes();
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/businessPartners/{businessPartnerId}/contacts/new", method = RequestMethod.GET)
    public String initCreationForm(@PathVariable("businessPartnerId") int businessPartnerId, Map<String, Object> model) {
        BusinessPartner businessPartner = this.calypsoService.findBusinessPartnerById(businessPartnerId);
        Contact contact = new Contact();
        businessPartner.addContact(contact);
        model.put("contact", contact);
        return "contacts/createOrUpdateContactForm";
    }

    @RequestMapping(value = "/businessPartners/{businessPartnerId}/contacts/new", method = RequestMethod.POST)
    public String processCreationForm(@ModelAttribute("contact") Contact contact, BindingResult result, SessionStatus status) {
        new ContactValidator().validate(contact, result);
        if (result.hasErrors()) {
            return "contacts/createOrUpdateContactForm";
        } else {
            this.calypsoService.saveContact(contact);
            status.setComplete();
            return "redirect:/businessPartners/{businessPartnerId}";
        }
    }

    @RequestMapping(value = "/businessPartners/*/contacts/{contactId}/edit", method = RequestMethod.GET)
    public String initUpdateForm(@PathVariable("contactId") int contactId, Map<String, Object> model) {
        Contact contact = this.calypsoService.findContactById(contactId);
        model.put("contact", contact);
        return "contacts/createOrUpdateContactForm";
    }

    @RequestMapping(value = "/businessPartners/{businessPartnerId}/contacts/{contactId}/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public String processUpdateForm(@ModelAttribute("contact") Contact contact, BindingResult result, SessionStatus status) {
        // we're not using @Valid annotation here because it is easier to define such validation rule in Java
        new ContactValidator().validate(contact, result);
        if (result.hasErrors()) {
            return "contacts/createOrUpdateContactForm";
        } else {
            this.calypsoService.saveContact(contact);
            status.setComplete();
            return "redirect:/businessPartners/{businessPartnerId}";
        }
    }

}
