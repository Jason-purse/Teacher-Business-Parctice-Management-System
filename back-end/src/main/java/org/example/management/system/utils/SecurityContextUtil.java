package org.example.management.system.utils;

import com.generatera.security.authorization.server.specification.LightningUserContext;
import org.example.management.system.MYLightningUserPrincipal;

import java.util.Optional;

public class SecurityContextUtil {

    public static Optional<MYLightningUserPrincipal> getCurrentUserInfo() {
        return LightningUserContext.get()
                .getUserPrincipal(MYLightningUserPrincipal.class);
    }
}
