package org.example.management.system;

import com.generatera.security.authorization.server.specification.LightningUserPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MYLightningUserPrincipal implements LightningUserPrincipal {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
