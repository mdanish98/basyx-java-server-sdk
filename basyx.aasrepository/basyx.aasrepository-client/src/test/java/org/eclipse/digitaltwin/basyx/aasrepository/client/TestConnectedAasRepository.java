/*******************************************************************************
 * Copyright (C) 2024 the Eclipse BaSyx Authors
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


package org.eclipse.digitaltwin.basyx.aasrepository.client;

import java.io.IOException;

import org.eclipse.digitaltwin.aas4j.v3.model.AssetAdministrationShell;
import org.eclipse.digitaltwin.basyx.aasrepository.AasFilter;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepository;
import org.eclipse.digitaltwin.basyx.aasrepository.AasRepositorySuite;
import org.eclipse.digitaltwin.basyx.aasrepository.http.DummyAasRepositoryComponent;
import org.eclipse.digitaltwin.basyx.aasservice.AasService;
import org.eclipse.digitaltwin.basyx.aasservice.DummyAssetAdministrationShellFactory;
import org.eclipse.digitaltwin.basyx.core.pagination.PaginationInfo;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Features of the client not implemented but existing in the test suite are
 * overwritten to pass. This is required to enable reuse of the test suite.
 * Whenever a feature is implemented, the respective test here has to be
 * removed.
 * 
 * @author schnicke, mateusmolina
 */
public class TestConnectedAasRepository extends AasRepositorySuite {

	private static ConfigurableApplicationContext appContext;

	@BeforeClass
	public static void startAASRepo() throws Exception {
		appContext = new SpringApplicationBuilder(DummyAasRepositoryComponent.class).profiles("httptests").run(new String[] {});
	}

	@After
	public void removeAasFromRepo() {
		AasRepository repo = appContext.getBean(AasRepository.class);
		repo.getAllAas(PaginationInfo.NO_LIMIT, new AasFilter()).getResult().stream().map(s -> s.getId()).forEach(repo::deleteAas);
	}

	@AfterClass
	public static void shutdownAASRepo() {
		appContext.close();
	}
	
	@Override
	@Test
	@Ignore
	public void retrieveAllAasWithSpecificAssetIdAndIdShortFiltering() throws Exception {
		//TODO: Clients doesn't support filtering, remove this when client has the functionality
	}
	
	@Override
	@Test
	@Ignore
	public void retrieveAllAasWithIdFiltering() throws Exception {
		//TODO: Clients doesn't support filtering, remove this when client has the functionality
	}
	
	@Override
	@Test
	@Ignore
	public void retrieveAllAasWithIdShortFiltering() throws Exception {
		//TODO: Clients doesn't support filtering, remove this when client has the functionality
	}
	
	@Override
	@Test
	@Ignore
	public void retrieveAllAasWithSpecificAssetIdFiltering() throws Exception {
		//TODO: Clients doesn't support filtering, remove this when client has the functionality
	}

	@Override
	protected AasRepository getAasRepository() {
		return new ConnectedAasRepository("http://localhost:8080");
	}
	
	@Override
	protected AasService getAasServiceWithThumbnail() throws IOException {
		AssetAdministrationShell expected = DummyAssetAdministrationShellFactory.createForThumbnail();
		AasService aasServiceWithThumbnail = getAasService(expected);
	
		aasServiceWithThumbnail.setThumbnail("dummyImgA.jpeg", "", createDummyImageIS_A());
		
		return aasServiceWithThumbnail;
	}
}
