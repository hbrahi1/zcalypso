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

import org.springframework.dao.DataAccessException;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Pet;
import com.calypso.model.PetType;
import com.calypso.model.Vet;
import com.calypso.model.Visit;


/**
 * Mostly used as a facade for all controllers
 *
 */
public interface CalypsoService {

    Collection<PetType> findPetTypes() throws DataAccessException;

    BusinessPartner findBusinessPartnerById(int id) throws DataAccessException;

    Pet findPetById(int id) throws DataAccessException;
    
    Collection<Pet> findPetByName(String name) throws DataAccessException;

    void savePet(Pet pet) throws DataAccessException;

    void saveVisit(Visit visit) throws DataAccessException;

    Collection<Vet> findVets() throws DataAccessException;

    void saveBusinessPartner(BusinessPartner businessPartner) throws DataAccessException;

    Collection<BusinessPartner> findBusinessPartnerByLastName(String lastName) throws DataAccessException;

	Collection<Pet> findPets() throws DataAccessException;

}
