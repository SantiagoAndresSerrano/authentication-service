package ufps.edu.co.authentication.domain.model.passwordreset.gateway;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;

import java.util.List;

public interface PasswordResetTokenService {
    public List<PasswordResetToken> listar();
    public PasswordResetToken buscarToken(String token);
    public void guardar(PasswordResetToken ct);
    public void eliminar(PasswordResetToken ct);

    public void eliminarByToken(String token);

}