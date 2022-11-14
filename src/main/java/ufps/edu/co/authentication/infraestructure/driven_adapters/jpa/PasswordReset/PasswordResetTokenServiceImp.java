package ufps.edu.co.authentication.infraestructure.driven_adapters.jpa.PasswordReset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;
import ufps.edu.co.authentication.domain.model.passwordreset.gateway.PasswordResetTokenService;

import java.util.List;

@Service
public class PasswordResetTokenServiceImp implements PasswordResetTokenService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenDAO;

    @Override
    @Transactional(readOnly = true)
    public List<PasswordResetToken> listar() {
        return passwordResetTokenDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordResetToken buscarToken(String token) {
        return passwordResetTokenDAO.findPasswordResetTokenByToken(token);
    }

    @Override
    @Transactional
    public void guardar(PasswordResetToken ct) {
        passwordResetTokenDAO.save(ct);
    }

    @Override
    @Transactional
    public void eliminar(PasswordResetToken ct) {
        passwordResetTokenDAO.delete(ct);
    }

    @Override
    @Transactional
    public void eliminarByToken(String token) {
        passwordResetTokenDAO.deletePasswordResetTokenByToken(token);
    }
}
