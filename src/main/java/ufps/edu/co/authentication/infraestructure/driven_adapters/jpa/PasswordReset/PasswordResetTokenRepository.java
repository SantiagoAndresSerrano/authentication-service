package ufps.edu.co.authentication.infraestructure.driven_adapters.jpa.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    PasswordResetToken findPasswordResetTokenByToken(String token);
    @Modifying
    @Query("delete from PasswordResetToken prt where prt.token=:token")
    void deletePasswordResetTokenByToken(@Param("token") String token);
}

