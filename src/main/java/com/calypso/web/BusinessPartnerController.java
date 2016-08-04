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

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.calypso.model.BusinessPartner;
import com.calypso.service.CalypsoService;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
@SessionAttributes(types = BusinessPartner.class)
public class BusinessPartnerController {

    private final CalypsoService calypsoService;


    @Autowired
    public BusinessPartnerController(CalypsoService calypsoService) {
        this.calypsoService = calypsoService;
    }

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/businessPartners/new", method = RequestMethod.GET)
    public String initCreationForm(Map<String, Object> model) {
        BusinessPartner businessPartner = new BusinessPartner();
        model.put("businessPartner", businessPartner);
        return "businessPartners/createOrUpdateBusinessPartnerForm";
    }

    @RequestMapping(value = "/businessPartners/new", method = RequestMethod.POST)
    public String processCreationForm(@Valid BusinessPartner businessPartner, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "businessPartners/createOrUpdateBusinessPartnerForm";
        } else {
            this.calypsoService.saveBusinessPartner(businessPartner);
            status.setComplete();
            return "redirect:/businessPartners/" + businessPartner.getId();
        }
    }

    @RequestMapping(value = "/businessPartners/find", method = RequestMethod.GET)
    public String initFindForm(Map<String, Object> model) {
        model.put("businessPartner", new BusinessPartner());
        return "businessPartners/findBusinessPartners";
    }

    @RequestMapping(value = "/businessPartners", method = RequestMethod.GET)
    public String processFindForm(BusinessPartner businessPartner, BindingResult result, Map<String, Object> model) {

        // allow parameterless GET request for /businessPartners to return all records
        if (businessPartner.getLastName() == null) {
            businessPartner.setLastName(""); // empty string signifies broadest possible search
        }

        // find businessPartners by last name
        Collection<BusinessPartner> results = this.calypsoService.findBusinessPartnerByLastName(businessPartner.getLastName());
        if (results.size() < 1) {
            // no businessPartners found
            result.rejectValue("lastName", "notFound", "not found");
            return "businessPartners/findBusinessPartners";
        }
        if (results.size() > 1) {
            // multiple businessPartners found
            model.put("selections", results);
            return "businessPartners/businessPartnersList";
        } else {
            // 1 businessPartner found
            businessPartner = results.iterator().next();
            return "redirect:/businessPartners/" + businessPartner.getId();
        }
    }

    @RequestMapping(value = "/businessPartners/{businessPartnerId}/edit", method = RequestMethod.GET)
    public String initUpdateBusinessPartnerForm(@PathVariable("businessPartnerId") int businessPartnerId, Model model) {
        BusinessPartner businessPartner = this.calypsoService.findBusinessPartnerById(businessPartnerId);
        model.addAttribute(businessPartner);
        return "businessPartners/createOrUpdateBusinessPartnerForm";
    }

    @RequestMapping(value = "/businessPartners/{businessPartnerId}/edit", method = RequestMethod.PUT)
    public String processUpdateBusinessPartnerForm(@Valid BusinessPartner businessPartner, BindingResult result, SessionStatus status) {
        if (result.hasErrors()) {
            return "businessPartners/createOrUpdateBusinessPartnerForm";
        } else {
            this.calypsoService.saveBusinessPartner(businessPartner);
            status.setComplete();
            return "redirect:/businessPartners/{businessPartnerId}";
        }
    }

    /**
     * Custom handler for displaying an businessPartner.
     *
     * @param businessPartnerId the ID of the businessPartner to display
     * @return a ModelMap with the model attributes for the view
     */
    @RequestMapping("/businessPartners/{businessPartnerId}")
    public ModelAndView showBusinessPartner(@PathVariable("businessPartnerId") int businessPartnerId) {
        ModelAndView mav = new ModelAndView("businessPartners/businessPartnerDetails");
        mav.addObject(this.calypsoService.findBusinessPartnerById(businessPartnerId));
        return mav;
    }

}
