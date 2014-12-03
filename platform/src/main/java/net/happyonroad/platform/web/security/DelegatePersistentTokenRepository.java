/**
 * @author XiongJie, Date: 14-7-24
 */
package net.happyonroad.platform.web.security;

import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.util.Assert;

import java.util.Date;

/** Description */
public class DelegatePersistentTokenRepository implements PersistentTokenRepository {

    PersistentTokenRepository delegate;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        Assert.notNull(delegate);
        delegate.createNewToken(token);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        Assert.notNull(delegate);
        delegate.updateToken(series, tokenValue, lastUsed);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        Assert.notNull(delegate);
        return delegate.getTokenForSeries(seriesId);
    }

    @Override
    public void removeUserTokens(String username) {
        Assert.notNull(delegate);
        delegate.removeUserTokens(username);
    }

    public void setDelegate(PersistentTokenRepository delegate) {
        Assert.notNull(delegate);
        this.delegate = delegate;
    }
}
