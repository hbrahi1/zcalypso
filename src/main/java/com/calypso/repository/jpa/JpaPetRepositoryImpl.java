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
package com.calypso.repository.jpa;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.calypso.model.Pet;
import com.calypso.model.PetType;
import com.calypso.repository.PetRepository;

/**
 * JPA implementation of the {@link PetRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
public class JpaPetRepositoryImpl implements PetRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<PetType> findPetTypes() {
        return this.em.createQuery("SELECT ptype FROM PetType ptype ORDER BY ptype.name").getResultList();
    }

    @Override
    public Pet findById(int id) {
        return this.em.find(Pet.class, id);
    }

    @Override
    public void save(Pet pet) {
    	if (pet.getId() == null) {
    		this.em.persist(pet);     		
    	}
    	else {
    		this.em.merge(pet);    
    	}
    }

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Pet> findAll() throws DataAccessException {
		return this.em.createQuery("SELECT pet FROM Pet pet").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Pet> findByName(String query) throws DataAccessException {
		return this.em.createQuery("SELECT pet FROM Pet pet WHERE UPPER(pet.name) like UPPER('%" + query + "%')").getResultList();
	}

}