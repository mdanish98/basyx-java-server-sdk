/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.aasdiscoveryservice.backend.inmemory;

import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryService;
import org.eclipse.digitaltwin.basyx.aasdiscoveryservice.core.AasDiscoveryServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * {@link AasDiscoveryService} factory returning an in-memory backend {@link AasDiscoveryService}
 * 
 * @author zhangzai
 */
@Component
@ConditionalOnExpression("'${basyx.backend}'.equals('InMemory')")
public class InMemoryAasDiscoveryServiceFactory implements AasDiscoveryServiceFactory {
	
	private String aasDiscoveryServiceName;
	
	@Autowired(required = false)
	public InMemoryAasDiscoveryServiceFactory() { }
	
	@Autowired(required = false)
	public InMemoryAasDiscoveryServiceFactory(@Value("${basyx.aasdiscserv.name:aas-discovery-service}") String aasDiscoveryServiceName) { 
		this.aasDiscoveryServiceName = aasDiscoveryServiceName;
	}
	
	@Override
	public AasDiscoveryService create() {
		return new InMemoryAasDiscoveryService(aasDiscoveryServiceName);
	}

}