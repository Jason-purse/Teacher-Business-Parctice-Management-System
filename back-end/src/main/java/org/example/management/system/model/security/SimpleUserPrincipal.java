package org.example.management.system.model.security;

import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import org.example.management.system.model.entity.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class SimpleUserPrincipal implements LightningUserPrincipal  {
    private final List<GrantedAuthority> authorities;

    private final User user;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    private final boolean enable;

    public SimpleUserPrincipal(
            User user,
            List<GrantedAuthority> authorities,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enable
    ) {
        this.user = user;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enable = enable;
    }

    public static SimpleUserPrincipal unAuthenticated(
            User user,
            List<GrantedAuthority> authorities
    ) {
        return new SimpleUserPrincipal(user,authorities,false,false,false,false);
    }

    public static SimpleUserPrincipal authenticated(
            User user,
            List<GrantedAuthority> authorities
    ) {
        return new SimpleUserPrincipal(user,authorities,true,true,true,true);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enable;
    }

    public User getUser() {
        return this.user;
    }
}
