// Copyright 2018 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.athenz.zpe;

import com.yahoo.athenz.zpe.AuthZpeClient;
import com.yahoo.vespa.athenz.api.AthenzResourceName;
import com.yahoo.vespa.athenz.api.AthenzRole;
import com.yahoo.vespa.athenz.api.ZToken;
import com.yahoo.vespa.athenz.zpe.AuthorizationResult.Type;

import java.security.cert.X509Certificate;

/**
 * The default implementation of {@link Zpe}.
 * This implementation is currently based on the official Athenz ZPE library.
 *
 * @author bjorncs
 */
public class DefaultZpe implements Zpe {

    public DefaultZpe() {
        AuthZpeClient.init();
    }

    @Override
    public AuthorizationResult checkAccessAllowed(ZToken roleToken, AthenzResourceName resourceName, String action) {
        StringBuilder returnedMatchedRole = new StringBuilder();
        AuthZpeClient.AccessCheckStatus rawResult =
                AuthZpeClient.allowAccess(roleToken.getRawToken(), resourceName.toResourceNameString(), action, returnedMatchedRole);
        return createResult(returnedMatchedRole, rawResult, resourceName);
    }

    @Override
    public AuthorizationResult checkAccessAllowed(X509Certificate roleCertificate, AthenzResourceName resourceName, String action) {
        StringBuilder returnedMatchedRole = new StringBuilder();
        AuthZpeClient.AccessCheckStatus rawResult =
                AuthZpeClient.allowAccess(roleCertificate, resourceName.toResourceNameString(), action, returnedMatchedRole);
        return createResult(returnedMatchedRole, rawResult, resourceName);
    }

    private static AuthorizationResult createResult(
            StringBuilder matchedRole, AuthZpeClient.AccessCheckStatus rawResult, AthenzResourceName resourceName) {
        return new AuthorizationResult(Type.fromAccessCheckStatus(rawResult), toRole(matchedRole, resourceName));
    }

    private static AthenzRole toRole(StringBuilder rawRole, AthenzResourceName resourceName) {
        if (rawRole.length() == 0) {
            return null;
        } else {
            return new AthenzRole(resourceName.getDomain(), rawRole.toString());
        }
    }

}
