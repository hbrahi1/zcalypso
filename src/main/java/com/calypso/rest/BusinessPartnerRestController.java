/*
 * Copyright 2002-2014 the original author or authors.
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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.calypso.model.BusinessPartner;
import com.calypso.service.CalypsoService;

/**
 * @author lim.han
 */
@RestController
@RequestMapping("/api")
public class BusinessPartnerRestController {

    private final CalypsoService calypsoService;

    @Autowired
    public BusinessPartnerRestController(CalypsoService calypsoService) {
        this.calypsoService = calypsoService;
    }

    @RequestMapping(value = "/businessPartners", method = RequestMethod.POST)
    public @ResponseBody BusinessPartner create(@RequestBody BusinessPartner businessPartner) {
    	if (businessPartner.getId()!=null && businessPartner.getId()>0){
    		BusinessPartner existingBusinessPartner = calypsoService.findBusinessPartnerById(businessPartner.getId());
    		BeanUtils.copyProperties(businessPartner, existingBusinessPartner, "pets", "id");
    		calypsoService.saveBusinessPartner(existingBusinessPartner);
    	}
    	else {
    		this.calypsoService.saveBusinessPartner(businessPartner);
    	}
    	
    	return businessPartner;
    }
    
    @RequestMapping(value = "/businessPartners", method = RequestMethod.PUT)
    public @ResponseBody Collection<BusinessPartner> create(@RequestBody Collection<BusinessPartner> businessPartners) {
    	for(BusinessPartner businessPartner : businessPartners) {
    		this.calypsoService.saveBusinessPartner(businessPartner);
    	}
    	
    	return businessPartners;
    }

    @RequestMapping(value = "/businessPartners/{id}", method = RequestMethod.GET)
    public @ResponseBody BusinessPartner find(@PathVariable Integer id) {
        return this.calypsoService.findBusinessPartnerById(id);
    }

    @RequestMapping(value = "/businessPartners", method = RequestMethod.GET)
    public @ResponseBody Collection<BusinessPartner> findByLastName(@RequestParam(defaultValue="") String lastName) {
        return this.calypsoService.findBusinessPartnerByLastName(lastName);
    }
}
