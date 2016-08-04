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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Pet;
import com.calypso.model.PetType;
import com.calypso.model.Vet;
import com.calypso.model.Visit;
import com.calypso.service.CalypsoService;
import com.calypso.util.EntityUtils;

/**
 * <p> Base class for {@link CalypsoService} integration tests. </p> <p> Subclasses should specify Spring context
 * configuration using {@link ContextConfiguration @ContextConfiguration} annotation </p> <p>
 * AbstractcalypsoServiceTests and its subclasses benefit from the following services provided by the Spring
 * TestContext Framework: </p> <ul> <li><strong>Spring IoC container caching</strong> which spares us unnecessary set up
 * time between test execution.</li> <li><strong>Dependency Injection</strong> of test fixture instances, meaning that
 * we don't need to perform application context lookups. See the use of {@link Autowired @Autowired} on the <code>{@link
 * AbstractcalypsoServiceTests#calypsoService calypsoService}</code> instance variable, which uses autowiring <em>by
 * type</em>. <li><strong>Transaction management</strong>, meaning each test method is executed in its own transaction,
 * which is automatically rolled back by default. Thus, even if tests insert or otherwise change database state, there
 * is no need for a teardown or cleanup script. <li> An {@link org.springframework.context.ApplicationContext
 * ApplicationContext} is also inherited and can be used for explicit bean lookup if necessary. </li> </ul>
 *
 * @author Ken Krebs
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Michael Isvy
 */
public abstract class AbstractClinicServiceTests {

    @Autowired
    protected CalypsoService calypsoService;

    @Test
    @Transactional
    public void findBusinessPartners() {
        Collection<BusinessPartner> businessPartners = this.calypsoService.findBusinessPartnerByLastName("Davis");
        assertEquals(2, businessPartners.size());
        businessPartners = this.calypsoService.findBusinessPartnerByLastName("Daviss");
        assertEquals(0, businessPartners.size());
    }

    @Test
    public void findSingleBusinessPartner() {
        BusinessPartner businessPartner1 = this.calypsoService.findBusinessPartnerById(1);
        assertTrue(businessPartner1.getLastName().startsWith("Franklin"));
        BusinessPartner businessPartner10 = this.calypsoService.findBusinessPartnerById(10);
        assertEquals("Carlos", businessPartner10.getFirstName());

        assertEquals(businessPartner1.getPets().size(), 1);
    }

    @Test
    @Transactional
    public void insertBusinessPartner() {
        Collection<BusinessPartner> businessPartners = this.calypsoService.findBusinessPartnerByLastName("Schultz");
        int found = businessPartners.size();
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setFirstName("Sam");
        businessPartner.setLastName("Schultz");
        businessPartner.setAddress("4, Evans Street");
        businessPartner.setCity("Wollongong");
        businessPartner.setTelephone("4444444444");
        this.calypsoService.saveBusinessPartner(businessPartner);
        Assert.assertNotEquals("BusinessPartner Id should have been generated", businessPartner.getId().longValue(), 0);
        businessPartners = this.calypsoService.findBusinessPartnerByLastName("Schultz");
        assertEquals("Verifying number of businessPartners after inserting a new one.", found + 1, businessPartners.size());
    }

    @Test
    @Transactional
    public void updateBusinessPartner() throws Exception {
        BusinessPartner o1 = this.calypsoService.findBusinessPartnerById(1);
        String old = o1.getLastName();
        o1.setLastName(old + "X");
        this.calypsoService.saveBusinessPartner(o1);
        o1 = this.calypsoService.findBusinessPartnerById(1);
        assertEquals(old + "X", o1.getLastName());
    }

	@Test
	public void findPet() {
	    Collection<PetType> types = this.calypsoService.findPetTypes();
	    Pet pet7 = this.calypsoService.findPetById(7);
	    assertTrue(pet7.getName().startsWith("Samantha"));
	    assertEquals(EntityUtils.getById(types, PetType.class, 1).getId(), pet7.getType().getId());
	    assertEquals("Jean", pet7.getBusinessPartner().getFirstName());
	    Pet pet6 = this.calypsoService.findPetById(6);
	    assertEquals("George", pet6.getName());
	    assertEquals(EntityUtils.getById(types, PetType.class, 4).getId(), pet6.getType().getId());
	    assertEquals("Peter", pet6.getBusinessPartner().getFirstName());
	}

	@Test
	public void getPetTypes() {
	    Collection<PetType> petTypes = this.calypsoService.findPetTypes();
	
	    PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1);
	    assertEquals("cat", petType1.getName());
	    PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4);
	    assertEquals("snake", petType4.getName());
	}

	@Test
	@Transactional
	public void insertPet() {
	    BusinessPartner businessPartner6 = this.calypsoService.findBusinessPartnerById(6);
	    int found = businessPartner6.getPets().size();
	    Pet pet = new Pet();
	    pet.setName("bowser");
	    Collection<PetType> types = this.calypsoService.findPetTypes();
	    pet.setType(EntityUtils.getById(types, PetType.class, 2));
	    pet.setBirthDate(new DateTime());
	    businessPartner6.addPet(pet);
	    assertEquals(found + 1, businessPartner6.getPets().size());
	    // both storePet and storeBusinessPartner are necessary to cover all ORM tools
	    this.calypsoService.savePet(pet);
	    this.calypsoService.saveBusinessPartner(businessPartner6);
	    businessPartner6 = this.calypsoService.findBusinessPartnerById(6);
	    assertEquals(found + 1, businessPartner6.getPets().size());
	    assertNotNull("Pet Id should have been generated", pet.getId());
	}

	@Test
	@Transactional
	public void updatePet() throws Exception {
	    Pet pet7 = this.calypsoService.findPetById(7);
	    String old = pet7.getName();
	    pet7.setName(old + "X");
	    this.calypsoService.savePet(pet7);
	    pet7 = this.calypsoService.findPetById(7);
	    assertEquals(old + "X", pet7.getName());
	}

	@Test
	public void findVets() {
	    Collection<Vet> vets = this.calypsoService.findVets();
	
	    Vet v1 = EntityUtils.getById(vets, Vet.class, 2);
	    assertEquals("Leary", v1.getLastName());
	    assertEquals(1, v1.getNrOfSpecialties());
	    assertEquals("radiology", (v1.getSpecialties().get(0)).getName());
	    Vet v2 = EntityUtils.getById(vets, Vet.class, 3);
	    assertEquals("Douglas", v2.getLastName());
	    assertEquals(2, v2.getNrOfSpecialties());
	    assertEquals("dentistry", (v2.getSpecialties().get(0)).getName());
	    assertEquals("surgery", (v2.getSpecialties().get(1)).getName());
	}

	@Test
	@Transactional
	public void insertVisit() {
	    Pet pet7 = this.calypsoService.findPetById(7);
	    int found = pet7.getVisits().size();
	    Visit visit = new Visit();
	    pet7.addVisit(visit);
	    visit.setDescription("test");
	    // both storeVisit and storePet are necessary to cover all ORM tools
	    this.calypsoService.saveVisit(visit);
	    this.calypsoService.savePet(pet7);
	    pet7 = this.calypsoService.findPetById(7);
	    assertEquals(found + 1, pet7.getVisits().size());
	    assertNotNull("Visit Id should have been generated", visit.getId());
	}


}
