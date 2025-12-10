package com.tareasCliente.views.Controller;

import com.tareasCliente.views.ML.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import com.tareasCliente.views.ML.Usuario;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlller {

    @GetMapping("/perfil")
    public String verPerfil(Model model, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        String username = (String) session.getAttribute("username");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (username == null || idUsuario == null) {
            model.addAttribute("error", "Debes iniciar sesión primero");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        try {
            String token = (String) session.getAttribute("token");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Result<Usuario>> response = restTemplate.exchange(
                    "http://localhost:8080/api/usuario/" + idUsuario,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Result<Usuario>>() {
            }
            );

            Result<Usuario> result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null && result.object != null) {
                Usuario usuario = result.object;
                model.addAttribute("usuario", usuario);
                model.addAttribute("username", username);
                return "usuario/perfil";
            } else {
                model.addAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

        } catch (Exception ex) {
            model.addAttribute("error", "Error al cargar perfil: " + ex.getMessage());
            return "redirect:/auth/login";
        }
    }
    
    @GetMapping("/editar")
public String FormularioEditar(Model model, HttpSession session) {
    RestTemplate restTemplate = new RestTemplate();

    String username = (String) session.getAttribute("username");
    Integer idUsuario = (Integer) session.getAttribute("idUsuario");

    if (username == null || idUsuario == null) {
        model.addAttribute("error", "Debes iniciar sesión primero");
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    try {
        String token = (String) session.getAttribute("token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<Result<Usuario>> response = restTemplate.exchange(
                "http://localhost:8080/api/usuario/" + idUsuario,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Result<Usuario>>() {}
        );

        Result<Usuario> result = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && result != null && result.object != null) {
            Usuario usuario = result.object;
            model.addAttribute("usuario", usuario);
            model.addAttribute("idUsuario", idUsuario);
            model.addAttribute("username", username);
            return "usuario/editar";
        } else {
            model.addAttribute("error", "Usuario no encontrado");
            return "redirect:/usuario/perfil";
        }

    } catch (Exception ex) {
        model.addAttribute("error", "Error al cargar datos: " + ex.getMessage());
        return "redirect:/usuario/perfil";
    }
}

//    @GetMapping("/editar")
//    public String FormularioEditar(Model model, HttpSession session) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        String username = (String) session.getAttribute("username");
//        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
//
//        if (username == null || idUsuario == null) {
//            model.addAttribute("error", "Debes iniciar sesión primero");
//            model.addAttribute("usuario", new Usuario());
//            return "login";
//        }
//
//        try {
//            String token = (String) session.getAttribute("token");
//            HttpHeaders headers = new HttpHeaders();
//            headers.setBearerAuth(token);
//            HttpEntity<?> request = new HttpEntity<>(headers);
//
//            ResponseEntity<Result<Usuario>> response = restTemplate.exchange(
//                    "http://localhost:8080/api/usuario/" + idUsuario,
//                    HttpMethod.GET,
//                    request,
//                    new ParameterizedTypeReference<Result<Usuario>>() {
//            }
//            );
//
//            Result<Usuario> result = response.getBody();
//
//            if (response.getStatusCode().is2xxSuccessful() && result != null && result.object != null) {
//                Usuario usuario = result.object;
//                model.addAttribute("usuario", usuario);
//                model.addAttribute("username", username);
//                return "usuario/editar";
//
//            } else {
//                model.addAttribute("error", "Usuario no encontrado");
//                return "redirect:/usuario/perfil";
//            }
//
//        } catch (Exception ex) {
//            model.addAttribute("error", "Error al cargar datos: " + ex.getMessage());
//            return "redirect:/usuario/perfil";
//        }
//    }
    
//    @PostMapping("/editar")
//    public String actualizarUsuario(@ModelAttribute Usuario usuario, HttpSession session, Model model){
//    RestTemplate restTemplate = new RestTemplate();
//    
//    String username = (String) session.getAttribute("username");
//    Integer idUsuario = (Integer) session.getAttribute("idUsername");
//    
//    if(username == null || idUsuario == null){
//    
//    model.addAttribute("error", "Debes iniciar sesion primero");
//    model.addAttribute("usuario", new Usuario());
//    }
//    
//    try{
//        String token = (String) session.getAttribute("token");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(token);
//        
//        HttpEntity<Usuario> request = new HttpEntity<>(usuario,headers);
//        
//        ResponseEntity<Result<Usuario>> response = restTemplate.exchange(
//        "http://localhost:8080/api/usuario/actualizar/" + idUsuario,
//                HttpMethod.PUT,
//                request,
//                new ParameterizedTypeReference<Result<Usuario>>(){}
//        );
//        Result<Usuario> result = response.getBody();
//        
//        if(response.getStatusCode().is2xxSuccessful() && result != null){
//            session.setAttribute("username", result.object.getUsername());
//        model.addAttribute("success", "Usuario actualizado exitosamente");
//        return "redirect:/usuario/perfil";
//        }else{
//        model.addAttribute("error", result !=null ? result.errorMessage : "Error al actualizar usuario"); 
//        }
//        
//    }catch(Exception ex){
//                model.addAttribute("error", "Error al actualizar usuario: " + ex.getMessage());
//    }
//    
//    
//    model.addAttribute("usuario", usuario);
//    model.addAttribute("username",username);
//    return "usuario/editar";
//    }
    
    @PostMapping("/eliminar")
    public String eliminarUsuario(HttpSession session, Model model) {
        RestTemplate restTemplate = new RestTemplate();

        String username = (String) session.getAttribute("username");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (username == null || idUsuario == null) {
            model.addAttribute("error", "Debes iniciar sesión primero");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }

        try {
            String token = (String) session.getAttribute("token");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Result<Usuario>> response = restTemplate.exchange(
                    "http://localhost:8080/api/usuario/eliminar/" + idUsuario,
                    HttpMethod.DELETE,
                    request,
                    new ParameterizedTypeReference<Result<Usuario>>() {}
            );

            Result<Usuario> result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                session.invalidate();
                model.addAttribute("success", "Usuario eliminado exitosamente");
                model.addAttribute("usuario", new Usuario());
                return "login";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Error al eliminar usuario");
                return "redirect:/usuario/perfil";
            }

        } catch (Exception ex) {
            model.addAttribute("error", "Error al eliminar usuario: " + ex.getMessage());
            return "redirect:/usuario/perfil";
        }   
    }
}
