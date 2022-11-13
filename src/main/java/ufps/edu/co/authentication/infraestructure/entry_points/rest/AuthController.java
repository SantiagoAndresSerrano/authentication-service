package ufps.edu.co.authentication.infraestructure.entry_points.rest;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ufps.edu.co.authentication.domain.model.jwt.JwtDto;
import ufps.edu.co.authentication.domain.model.passwordreset.PasswordResetToken;
import ufps.edu.co.authentication.domain.model.passwordreset.gateway.PasswordResetTokenService;
import ufps.edu.co.authentication.domain.model.rol.Rol;
import ufps.edu.co.authentication.domain.model.rol.gateway.RolService;
import ufps.edu.co.authentication.domain.model.user.LoginUsuario;
import ufps.edu.co.authentication.domain.model.user.Mensaje;
import ufps.edu.co.authentication.domain.model.user.NuevoUsuario;
import ufps.edu.co.authentication.domain.model.user.Usuario;
import ufps.edu.co.authentication.domain.model.user.gateway.UsuarioService;
import ufps.edu.co.authentication.infraestructure.config.EmailSenderServiceImp;
import ufps.edu.co.authentication.infraestructure.jwt.JwtProvider;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.*;

/**
 *
 * @author santi
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin
@Slf4j
public class AuthController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    RolService rolService;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    EmailSenderServiceImp emailServiceImp;


    @Autowired
    PasswordResetTokenService passwordResetTokenService;


    @Value("${uribackend}")
    private String urlBackend;

    @Value("${urifrontend}")
    private String urlFrontend;

    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) throws MessagingException {
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos mal puestos o email inválido"), HttpStatus.BAD_REQUEST);
        if(usuarioService.existsByEmail(nuevoUsuario.getEmail()))
            return new ResponseEntity(new Mensaje("ese email ya existe"), HttpStatus.BAD_REQUEST);

        Usuario usuario =
                new Usuario(nuevoUsuario.getEmail(),
                        passwordEncoder.encode(nuevoUsuario.getPassword()));


        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getByRolNombre(Rol.RolNombre.ROLE_USER).get());
        if(nuevoUsuario.getRoles().contains("admin"))
            roles.add(rolService.getByRolNombre(Rol.RolNombre.ROLE_ADMIN).get());

        usuario.setRoles(roles);
        usuario.setCelular(nuevoUsuario.getCelular());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setEmail(nuevoUsuario.getEmail());
        usuario.setConfirmationToken(UUID.randomUUID().toString());

        usuarioService.guardar(usuario);
        emailServiceImp.enviarEmail("Confirmación de cuenta ",
                "<!DOCTYPE html> \n" +
                        "                        <html lang=\\en\\> \n" +
                        "                         \n" +
                        "                        <head> \n" +
                        "                            <meta charset=\"UTF-8\"> \n" +
                        "                            <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> \n" +
                        "                            <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"\\> \n" +
                        "                            <title>Document</title> \n" +
                        "                        </head> \n" +
                        "                         \n" +
                        "                        <body style=\"width: 800px\"> \n" +
                        "                           <div style=\"width:800px\">\n" +
                        "    <div style=\"background-color:#e4230aa2;width:100%;padding:3rem 0\">\n" +
                        "        <div style=\"text-align:center;background-color:#ffffff;margin:0 auto;width:80%;border-radius:8px\">\n" +
                        "            <img style=\"margin-top:3rem;width:290px\" src=\"https://ingsistemas.cloud.ufps.edu.co/rsc/img/logo_vertical_ingsistemas_ht180.png\" alt=\"logo\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                        "            <p style=\"margin:1rem 0;font-size:25px\"><span class=\"il\">Confirmación</span> de <span class=\"il\">cuenta</span></p>\n" +
                        "            <p style=\"color:#424242\">Hola, <b>"+usuario.getNombre()+"</b>, Te has registrado en la " +
                "plataforma, por favor realiza el proceso de confirmación.\n" +
                        "            </p>\n" +
                        "            <div style=\"margin:2rem auto;width:120px;background-color:#e4230a;padding:8px;border-radius:6px\">\n" +
                        "                <a style=\"color:#ffffff;text-decoration:none\" href="+urlFrontend+
                        "/login/confirmation/"+usuario.getConfirmationToken()+" target=\"_blank\" " +
                        ">Continuar</a>\n" +
                        "            </div>\n" +
                        "            <div style=\"width:100%;border-top:2px solid #e4230a;padding:1rem 0\">\n" +
                        "                <p>Copyright © 2022 Microservicios <br> Todos los derechos reservados.</p><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                        "            </div></div><div class=\"adL\">\n" +
                        "        </div></div><div class=\"adL\">\n" +
                        "    </div></div><div class=\"adL\">\n" +
                        "</div></div>\n" +
                        "                        </body> \n" +
                        "                         \n" +
                        "                        </html>"
                ,
                usuario.getEmail()
        );


        return ResponseEntity.ok(new Mensaje("Confirmación de cuenta enviada al correo"));
    }

    @GetMapping("/solicitudPassword/{email}")
    public ResponseEntity<?> recuperarPassword(@PathVariable String email) throws MessagingException {
        Usuario u = usuarioService.findByEmail(email);

        if(u==null)
            return new ResponseEntity(new Mensaje("El email no existe"), HttpStatus.NOT_FOUND);


        if(u.passwordResetTokenCollection().size()>0){
            PasswordResetToken passwordResetToken = u.passwordResetTokenCollection().iterator().next();
            if(passwordResetToken.getFechaExpiracion().before(new Date())){
                passwordResetTokenService.eliminarByToken(passwordResetToken.getToken());
            }else{
                return new ResponseEntity(new Mensaje("Ya hay una solicitud de reestablecimiento pendiente"), HttpStatus.BAD_REQUEST);

            }
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken(u);
        passwordResetTokenService.guardar(passwordResetToken);

        emailServiceImp.enviarEmail("Cambio de contraseña",
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Document</title>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body style=\"width: 800px\">\n" +
                        "    <div style=\"background-color: #e4230aa2; width: 100%; padding: 3rem 0;\">\n" +
                        "        <div style=\"text-align: center; background-color: #ffffff; margin: 0 auto; width: 80%; border-radius: 8px;\">\n" +
                        "            <img style=\"margin-top:3rem;width:290px\" src=\"https://ingsistemas.cloud.ufps.edu.co/rsc/img/logo_vertical_ingsistemas_ht180.png\" alt=\"logo\" class=\"CToWUd\" data-bit=\"iit\">\n" +
                        "            <p style=\"margin: 1rem 0; font-size: 25px;\">Cambio de contraseña</p>\n" +
                        "            <p style=\"color: #424242;\">Hola, <b>"+u.getNombre()+"</b>, has solicitado cambiar tu contraseña, <br> para cambiar tu contraseña ingresa al siguiente link:  \n" +
                        "            </p>\n" +
                        "            <div style=\"margin: 2rem auto; width: 120px; background-color: #e4230a; padding: 8px; border-radius: 6px; \">\n" +
                        "                <a style=\"color: #ffffff; text-decoration: none\" href=\""+urlFrontend+
                        "/password-reset/confirmation/"+passwordResetToken.getToken()+"\">Continuar</a>\n" +
                        "            </div>\n" +
                        "            <div style=\"width: 100%; border-top: 2px solid #e4230a; padding: 1rem 0\">\n" +
                        "                <p>Copyright © 2022 Security Service <br> Todos los derechos reservados.</p>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>"

                ,
                u.getEmail());

        return new ResponseEntity(new Mensaje("Mensaje de recuperación enviado al correo"),HttpStatus.OK );
    }

    @GetMapping("/recuperar/{token}") //petición que recibe el backend de parte del frontend, recordar cambiar el link de la linea 131 a un URL del frontend
    public ResponseEntity<?>confirmarRecuperarPassword(@PathVariable String token){

        PasswordResetToken passwordResetToken = passwordResetTokenService.buscarToken(token);

        if(passwordResetToken == null)
            return new ResponseEntity(new Mensaje("El token no existe"), HttpStatus.NOT_FOUND);

        if(passwordResetToken.getFechaExpiracion().before(new Date()))
            return new ResponseEntity(new Mensaje("El token ha expirado"), HttpStatus.BAD_REQUEST);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/recuperar/{token}")
    public ResponseEntity<?>cambiarPassword(@PathVariable String token, @RequestBody LoginUsuario loginUsuario) throws MessagingException {

        PasswordResetToken passwordResetToken = passwordResetTokenService.buscarToken(token);

        if(passwordResetToken == null)
            return new ResponseEntity(new Mensaje("El token no existe"), HttpStatus.NOT_FOUND);

        if(passwordResetToken.getFechaExpiracion().before(new Date()))
            return new ResponseEntity(new Mensaje("El token ha expirado"), HttpStatus.BAD_REQUEST);


        Usuario u = usuarioService.findByEmail(passwordResetToken.getUsuario().getEmail());

        if(u==null){
            return new ResponseEntity(new Mensaje("El correo no existe"), HttpStatus.BAD_REQUEST);
        }

        Usuario uToken = usuarioService.findByResetPassword(token);

        if(uToken==null){
            return new ResponseEntity(new Mensaje("El token no está asociado a ningun usuario"), HttpStatus.BAD_REQUEST);
        }

        if(!u.getEmail().equals(uToken.getEmail())){
            return new ResponseEntity(new Mensaje("El token incorrecto"), HttpStatus.BAD_REQUEST);
        }

        emailServiceImp.enviarEmail("Contraseña actualizada",
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Document</title>\n" +
                        "</head>\n" +
                        "\n" +
                        "<body style=\"width: 800px\">\n" +
                        "    <div style=\"background-color: #e4230aa2; width: 100%; padding: 3rem 0;\">\n" +
                        "        <div style=\"text-align: center; background-color: #ffffff; margin: 0 auto; width: 80%; border-radius: 8px;\">\n" +
                        "            <img style=\"margin-top: 3rem; width: 190px\"\n" +
                        "                src=\"https://master.d1oc2nyuhwk984.amplifyapp.com/assets/images/logo.png\" alt=\"logo\">\n" +
                        "            <p style=\"margin: 1rem 0; font-size: 25px;\">Cambio de contraseña</p>\n" +
                        "            <p style=\"color: #424242;\">Hola, <b>"+u.getNombre()+"</b>, se ha cambiado tu contraseña en el sistema.  \n" +
                        "            </p>\n" +
                        "            <div style=\"width: 100%; border-top: 2px solid #e4230a; padding: 1rem 0\">\n" +
                        "                <p>Copyright © 2022 Security Service <br> Todos los derechos reservados.</p>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "\n" +
                        "</html>"

                ,
                u.getEmail());

        u.setPassword(passwordEncoder.encode(loginUsuario.getPassword()));
        usuarioService.guardar(u);
        passwordResetTokenService.eliminar(passwordResetToken);
        return new ResponseEntity(new Mensaje("Contraseña actualizada"),HttpStatus.OK );
    }

    @GetMapping("/confirmacion/{token}")
    public ResponseEntity<?> confirmarToken(@PathVariable String token){
        Usuario usuario = usuarioService.findByConfirmationToken(token);

        if(usuario==null){
            return new ResponseEntity(new Mensaje("Error, Token no encontrado"), HttpStatus.NOT_FOUND);
        }

        usuario.setEstado(true);
        usuarioService.guardar(usuario);

        return ResponseEntity.ok(new Mensaje("Usuario verificado correctamente"));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@Valid @RequestBody LoginUsuario loginUsuario, BindingResult bindingResult){
        if(bindingResult.hasErrors())
            return new ResponseEntity(new Mensaje("campos mal puestos"), HttpStatus.BAD_REQUEST);

        Usuario usuario = usuarioService.getByEmail(loginUsuario.getEmail()).orElse(null);

        if(usuario == null){
            return new ResponseEntity(new Mensaje("El nombre de usuario no existe"), HttpStatus.NOT_FOUND);
        }

        if(!usuario.isEstado()){
            return new ResponseEntity(new Mensaje("El usuario se encuentra deshabilitado"), HttpStatus.NOT_FOUND);
        }


        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usuario.getEmail(), loginUsuario.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());


        return new ResponseEntity(jwtDto, HttpStatus.OK);
    }
    
    @GetMapping("/user/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable int id){
        Usuario usuario = usuarioService.encontrar(id).orElse(null);
        if (usuario == null)
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(usuario);
    }
     @GetMapping("/user/{email}")
    public ResponseEntity<?> obtenerUsuarioPorEmail(@PathVariable String email){
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null)
            return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok(new UserIDDTO(usuario.getIdUsuario()));
    }
}
