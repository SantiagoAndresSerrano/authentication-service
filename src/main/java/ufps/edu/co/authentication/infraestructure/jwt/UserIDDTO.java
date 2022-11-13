package ufps.edu.co.authentication.infraestructure.jwt;

public class UserIDDTO {
    int idUsuario;

    public UserIDDTO(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}
