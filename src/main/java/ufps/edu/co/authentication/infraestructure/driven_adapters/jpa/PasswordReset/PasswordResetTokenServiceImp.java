package ufps.edu.co.authentication.infraestructure.driven_adapters.jpa.PasswordReset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;
import ufps.edu.co.authentication.domain.model.passwordreset.gateway.PasswordResetTokenService;

import java.util.List;

@Service
public class PasswordResetTokenServiceImp implements PasswordResetTokenService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenDAO;

    @Override
    public List<PasswordResetToken> listar() {
        return passwordResetTokenDAO.findAll();
    }

    @Override
    public PasswordResetToken buscarToken(String token) {
        return passwordResetTokenDAO.findPasswordResetTokenByToken(token);
    }

    @Override
    public void guardar(PasswordResetToken ct) {
        passwordResetTokenDAO.save(ct);
    }

    @Override
    public void eliminar(PasswordResetToken ct) {
        passwordResetTokenDAO.delete(ct);
    }
}
