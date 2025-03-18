package com.trading.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading.domain.USER_ROLE;
import com.trading.domain.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String fullName;

    @Email(message = "Invalid email format")
    @NotNull
    private String email;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number format")
    private String mobile; // Remove @NotNull

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private UserStatus status = UserStatus.PENDING;

    private boolean isVerified = false;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    private String picture;

    private USER_ROLE role = USER_ROLE.ROLE_USER;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}