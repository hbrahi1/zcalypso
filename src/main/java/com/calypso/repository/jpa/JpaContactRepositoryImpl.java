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

import com.calypso.model.Contact;
import com.calypso.model.ContactType;
import com.calypso.repository.ContactRepository;

/**
 * JPA implementation of the {@link ContactRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
public class JpaContactRepositoryImpl implements ContactRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("unchecked")
    public List<ContactType> findContactTypes() {
        return this.em.createQuery("SELECT ptype FROM ContactType ptype ORDER BY ptype.name").getResultList();
    }

    @Override
    public Contact findById(int id) {
        return this.em.find(Contact.class, id);
    }

    @Override
    public void save(Contact contact) {
    	if (contact.getId() == null) {
    		this.em.persist(contact);     		
    	}
    	else {
    		this.em.merge(contact);    
    	}
    }

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Contact> findAll() throws DataAccessException {
		return this.em.createQuery("SELECT contact FROM Contact contact").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Contact> findByName(String query) throws DataAccessException {
		return this.em.createQuery("SELECT contact FROM Contact contact WHERE UPPER(contact.name) like UPPER('%" + query + "%')").getResultList();
	}

}
