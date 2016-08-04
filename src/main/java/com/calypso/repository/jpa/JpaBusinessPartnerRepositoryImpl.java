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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Repository;

import com.calypso.model.BusinessPartner;
import com.calypso.repository.BusinessPartnerRepository;

/**
 * JPA implementation of the {@link BusinessPartnerRepository} interface.
 *
 * @author Mike Keith
 * @author Rod Johnson
 * @author Sam Brannen
 * @author Michael Isvy
 * @since 22.4.2006
 */
@Repository
public class JpaBusinessPartnerRepositoryImpl implements BusinessPartnerRepository {

    @PersistenceContext
    private EntityManager em;


    /**
     * Important: in the current version of this method, we load BusinessPartners with all their Contacts and Visits while 
     * we do not need Visits at all and we only need one property from the Contact objects (the 'name' property).
     * There are some ways to improve it such as:
     * - creating a Ligtweight class (example here: https://community.jboss.org/wiki/LightweightClass)
     * - Turning on lazy-loading and using {@link OpenSessionInViewFilter}
     */
    @SuppressWarnings("unchecked")
    public Collection<BusinessPartner> findByLastName(String lastName) {
        // using 'join fetch' because a single query should load both businessPartners and contacts
        // using 'left join fetch' because it might happen that an businessPartner does not have contacts yet
        Query query = this.em.createQuery("SELECT DISTINCT businessPartner FROM BusinessPartner businessPartner left join fetch businessPartner.contacts WHERE businessPartner.lastName LIKE :lastName");
        query.setParameter("lastName", lastName + "%");
        return query.getResultList();
    }

    @Override
    public BusinessPartner findById(int id) {
        // using 'join fetch' because a single query should load both businessPartners and contacts
        // using 'left join fetch' because it might happen that an businessPartner does not have contacts yet
        Query query = this.em.createQuery("SELECT businessPartner FROM BusinessPartner businessPartner left join fetch businessPartner.contacts WHERE businessPartner.id =:id");
        query.setParameter("id", id);
        return (BusinessPartner) query.getSingleResult();
    }


    @Override
    public void save(BusinessPartner businessPartner) {
    	if (businessPartner.getId() == null) {
    		this.em.persist(businessPartner);     		
    	}
    	else {
    		this.em.merge(businessPartner);    
    	}

    }

}
