package ufps.edu.co.authentication.infraestructure.driven_adapters.jpa.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findPasswordResetTokenByToken(String token);
}

