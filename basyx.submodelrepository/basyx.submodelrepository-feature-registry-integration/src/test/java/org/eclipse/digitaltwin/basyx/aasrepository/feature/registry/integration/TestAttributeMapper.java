/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eclipse.digitaltwin.basyx.aasregistry.client.model.AssetKind;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringNameType;
import org.eclipse.digitaltwin.basyx.aasregistry.client.model.LangStringTextType;
import org.eclipse.digitaltwin.basyx.aasrepository.feature.registry.integration.AttributeMapper;
import org.junit.Test;

/**
 * Test class for {@link AttributeMapper}
 * 
 * @author danish
 */
public class TestAttributeMapper {
	
	@Test
	public void mapDescriptions() {
		List<LangStringTextType> expectedDescriptions = RegistryIntegrationTestHelper.getAasRegLangStringTextTypes();
		
		List<LangStringTextType> actualDescriptions = new AttributeMapper().mapDescription(RegistryIntegrationTestHelper.getAas4jLangStringTextTypes());
		
		assertEquals(expectedDescriptions.size(), actualDescriptions.size());
		assertEquals(expectedDescriptions, actualDescriptions);
	}
	
	@Test
	public void mapDisplayNames() {
		List<LangStringNameType> expectedDisplayNames = RegistryIntegrationTestHelper.getAasRegLangStringNameTypes();
		
		List<LangStringNameType> actualDisplayNames = new AttributeMapper().mapDisplayName(RegistryIntegrationTestHelper.getAas4jLangStringNameTypes());
		
		assertEquals(expectedDisplayNames.size(), actualDisplayNames.size());
		assertEquals(expectedDisplayNames, actualDisplayNames);
	}
	
	@Test
	public void mapAssetKind() {
		AssetKind expectedAssetKind = RegistryIntegrationTestHelper.AASREG_ASSET_KIND;
		
		AssetKind actualAssetKind = new AttributeMapper().mapAssetKind(RegistryIntegrationTestHelper.AAS4J_ASSET_KIND);

		assertEquals(expectedAssetKind, actualAssetKind);
	}

}
