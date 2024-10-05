package com.trading.modal;

import com.trading.domain.VerificationType;
import lombok.Data;
import lombok.Getter;

@Data
public class TwoFactorAuth {
    private boolean isEnabled = false;
    @Getter
    private VerificationType sendTo;

    public boolean isEnabled() {
        return isEnabled;
    }

}
