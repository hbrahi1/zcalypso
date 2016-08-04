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
package com.calypso.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.model.Vet;
import com.calypso.model.Visit;
import com.calypso.repository.BusinessPartnerRepository;
import com.calypso.repository.ContactRepository;
import com.calypso.repository.VetRepository;
import com.calypso.repository.VisitRepository;

/**
 * Mostly used as a facade for all controllers
 * Also a placeholder for @Transactional and @Cacheable annotations
 *
 * @author 
 */
@Service
public class CalypsoServiceImpl implements CalypsoService {

    private ContactRepository contactRepository;
    private VetRepository vetRepository;
    private BusinessPartnerRepository businessPartnerRepository;
    private VisitRepository visitRepository;

    @Autowired
    public CalypsoServiceImpl(ContactRepository contactRepository, VetRepository vetRepository, BusinessPartnerRepository businessPartnerRepository, VisitRepository visitRepository) {
        this.contactRepository = contactRepository;
        this.vetRepository = vetRepository;
        this.businessPartnerRepository = businessPartnerRepository;
        this.visitRepository = visitRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ContactType> findContactTypes() throws DataAccessException {
        return contactRepository.findContactTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessPartner findBusinessPartnerById(int id) throws DataAccessException {
        return businessPartnerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BusinessPartner> findBusinessPartnerByLastName(String lastName) throws DataAccessException {
        return businessPartnerRepository.findByLastName(lastName);
    }

    @Override
    @Transactional
    public void saveBusinessPartner(BusinessPartner businessPartner) throws DataAccessException {
        businessPartnerRepository.save(businessPartner);
    }


    @Override
    @Transactional
    public void saveVisit(Visit visit) throws DataAccessException {
        visitRepository.save(visit);
    }


    @Override
    @Transactional(readOnly = true)
    public Contact findContactById(int id) throws DataAccessException {
        return contactRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveContact(Contact contact) throws DataAccessException {
        contactRepository.save(contact);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "vets")
    public Collection<Vet> findVets() throws DataAccessException {
        return vetRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "contacts")
    public Collection<Contact> findContacts() throws DataAccessException {
        return contactRepository.findAll();
    }

	@Override
	public Collection<Contact> findContactByName(String name) throws DataAccessException {
		return contactRepository.findByName(name);
	}
}
