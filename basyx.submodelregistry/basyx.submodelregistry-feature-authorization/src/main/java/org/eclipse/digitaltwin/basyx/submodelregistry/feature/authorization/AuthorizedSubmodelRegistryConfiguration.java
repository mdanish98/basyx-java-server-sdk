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

package org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization;

import org.eclipse.digitaltwin.basyx.authorization.CommonAuthorizationProperties;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RbacStorage;
import org.eclipse.digitaltwin.basyx.authorization.rbac.RoleProvider;
import org.eclipse.digitaltwin.basyx.authorization.rbac.SimpleRbacPermissionResolver;
import org.eclipse.digitaltwin.basyx.authorization.rbac.TargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.authorization.rules.rbac.backend.submodel.TargetInformationAdapter;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.rbac.SubmodelRegistryTargetPermissionVerifier;
import org.eclipse.digitaltwin.basyx.submodelregistry.feature.authorization.rbac.backend.submodel.SubmodelRegistryTargetInformationAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for authorized {@link AuthorizedSubmodelRegistryStorage}
 *
 * @author danish
 */
@Configuration
@ConditionalOnExpression("#{${" + CommonAuthorizationProperties.ENABLED_PROPERTY_KEY + ":false}}")
public class AuthorizedSubmodelRegistryConfiguration {

	@Bean
	public TargetPermissionVerifier<SubmodelRegistryTargetInformation> getSubmodelRegistryTargetPermissionVerifier() {
		return new SubmodelRegistryTargetPermissionVerifier();
	}
	
	@Bean
	public RbacPermissionResolver<SubmodelRegistryTargetInformation> getSubmodelRegistryPermissionResolver(RbacStorage rbacStorage, RoleProvider roleProvider, TargetPermissionVerifier<SubmodelRegistryTargetInformation> targetPermissionVerifier) {
		return new SimpleRbacPermissionResolver<>(rbacStorage, roleProvider, targetPermissionVerifier);
	}
	
	@Bean
	public TargetInformationAdapter getSubmdoelRegistryTargetInformationAdapter() {

		return new SubmodelRegistryTargetInformationAdapter();
	}

}
