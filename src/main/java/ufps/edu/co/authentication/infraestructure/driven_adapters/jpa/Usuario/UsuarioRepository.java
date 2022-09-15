package ufps.edu.co.authentication.infraestructure.driven_adapters.jpa.Usuario;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author santi
 */
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ufps.edu.co.authentication.domain.model.user.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    Usuario findUsuarioByConfirmationToken(String token);

    @Query(value = "select pr.usuario from PasswordResetToken pr where pr.token=:token")
    Usuario encontrarUsuarioPorToken(@Param("token") String token);


}

