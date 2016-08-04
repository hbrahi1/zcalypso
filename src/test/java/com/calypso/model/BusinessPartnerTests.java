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
package com.calypso.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.calypso.model.BusinessPartner;
import com.calypso.model.Pet;

/**
 * JUnit test for the {@link BusinessPartner} class.
 *
 * @author Ken Krebs
 */
public class BusinessPartnerTests {

    @Test
    @Transactional
    public void testHasPet() {
        BusinessPartner businessPartner = new BusinessPartner();
        Pet fido = new Pet();
        fido.setName("Fido");
        assertNull(businessPartner.getPet("Fido"));
        assertNull(businessPartner.getPet("fido"));
        businessPartner.addPet(fido);
        assertEquals(fido, businessPartner.getPet("Fido"));
        assertEquals(fido, businessPartner.getPet("fido"));
    }

}
