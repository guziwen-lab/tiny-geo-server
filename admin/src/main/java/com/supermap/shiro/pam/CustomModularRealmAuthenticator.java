package com.supermap.shiro.pam;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class CustomModularRealmAuthenticator extends ModularRealmAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomModularRealmAuthenticator.class);

    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token) {
        AuthenticationStrategy strategy = this.getAuthenticationStrategy();
        AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Iterating through {} realms for PAM authentication", realms.size());
        }
        AuthenticationException exception = null;
        for (Realm realm : realms) {
            aggregate = strategy.beforeAttempt(realm, token, aggregate);
            if (realm.supports(token)) {
                LOGGER.trace("Attempting to authenticate token [{}] using realm [{}]", token, realm);
                AuthenticationInfo info = null;
                try {
                    info = realm.getAuthenticationInfo(token);
                } catch (AuthenticationException e) {
                    exception = e;
                    if (LOGGER.isDebugEnabled()) {
                        String msg = "Realm [" + realm + "] threw an exception during a multi-realm authentication attempt:";
                        LOGGER.debug(msg, e);
                    }
                }
                aggregate = strategy.afterAttempt(realm, token, info, aggregate, exception);
            } else {
                LOGGER.debug("Realm [{}] does not support token {}.  Skipping realm.", realm, token);
            }
        }
        if (exception != null) {
            throw exception;
        }
        aggregate = strategy.afterAllAttempts(token, aggregate);
        return aggregate;
    }

}