package com.tareasCliente.views.controller;

import com.tareasCliente.views.ML.Result;
import com.tareasCliente.views.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/auth")
public class AuthController {

 

    // ==================== REGISTRO ====================
    
    @GetMapping("/register")
    public String registroUsuarios(Model model){
        model.addAttribute("usuario", new Usuario());
        return "register";    
    }

    @PostMapping("/register")
    public String registro(@ModelAttribute Usuario usuario, Model model){
                        RestTemplate restTemplate = new RestTemplate();

        Result result = new Result();
        
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
            
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/register", 
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Result>(){}
            );
            
            result = response.getBody();
            
            if(response.getStatusCode().is2xxSuccessful() && result != null){
                model.addAttribute("success", "Usuario registrado exitosamente. Revisa tu correo para verificar tu cuenta");
                model.addAttribute("usuario", new Usuario());
                return "login";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Error al registrar usuario");
            }
        
        } catch(Exception ex){
            result.status = 500;
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            model.addAttribute("error", result.errorMessage);
        }
        
        model.addAttribute("usuario", usuario);
        return "register"; 
    }
    
    // ==================== VERIFICACIÓN ====================
     
    @GetMapping("/verify")
    public String verificarCuenta(@RequestParam String token, Model model){
        Result result = new Result();
                        RestTemplate restTemplate = new RestTemplate();

        
        try{
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/verify?token=" + token,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result>(){}
            );
        
            result = response.getBody();
            
            if(response.getStatusCode().is2xxSuccessful() && result != null){
                model.addAttribute("success", "Cuenta verificada correctamente. Ya puedes iniciar sesión");
                model.addAttribute("usuario", new Usuario());
                return "login";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Token inválido");
            }
            
        } catch(Exception ex){
            result.status = 500;
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.correct = false;
            model.addAttribute("error", result.errorMessage);
        }
        
        model.addAttribute("usuario", new Usuario());
        return "login";
    }

    // ==================== LOGIN ====================
    
    @GetMapping("/login")
    public String inicioSesion(Model model){
        model.addAttribute("usuario", new Usuario());
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@ModelAttribute Usuario usuario, 
                       Model model,
                       HttpSession session) {
        Result result = new Result();
                        RestTemplate restTemplate = new RestTemplate();

        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Usuario> request = new HttpEntity<>(usuario, headers);
            
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/login",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Result>(){}
            );
            
            result = response.getBody();
            
            if (response.getStatusCode().is2xxSuccessful() && result != null && result.object != null) {
                Map<String, Object> data = (Map<String, Object>) result.object;
                
                session.setAttribute("token", data.get("token"));
                session.setAttribute("username", data.get("username"));
                session.setAttribute("idUsuario", data.get("idUsuario"));

                
                model.addAttribute("success", "Inicio de sesión exitoso");
                model.addAttribute("username", data.get("username"));
                return "dashboard";
            } else {
            model.addAttribute("error", 
                result != null ? result.errorMessage : "Error al iniciar sesión");
        }
    } catch (HttpClientErrorException ex) {
        String errorBody = ex.getResponseBodyAsString();
        
        if (errorBody != null && !errorBody.isEmpty() && errorBody.contains("\"errorMessage\"")) {
            try {
                int start = errorBody.indexOf("\"errorMessage\":\"") + 16;
                int end = errorBody.indexOf("\"", start);
                String mensaje = errorBody.substring(start, end);
                model.addAttribute("warning", mensaje);
            } catch (Exception parseEx) {
                model.addAttribute("warning", "Debes verificar tu correo para validar tu cuenta");
            }
        } else {
            model.addAttribute("warning", "Debes verificar tu correo para validar tu cuenta");
        }
    } catch (HttpServerErrorException ex) {
        model.addAttribute("error", "Error en el servidor. Intenta de nuevo más tarde.");
    } catch (Exception ex) {
        model.addAttribute("error", "Error de conexión con el servidor");
    }
    
    model.addAttribute("usuario", usuario);
    return "login";
}

    // ==================== OLVIDÉ MI CONTRASEÑA ====================
    
    @GetMapping("/forgot")
    public String contraseñaOlvidada(){
        return "forgot";   
    }
    
    @PostMapping("/forgot")
    public String forgotPassword(@RequestParam String email, Model model){
        Result result = new Result();
                        RestTemplate restTemplate = new RestTemplate();

        
        try{
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/forgot?email=" + email, 
                HttpMethod.POST,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result>(){}
            );
            
            result = response.getBody();
           
            if(response.getStatusCode().is2xxSuccessful() && result != null){
                model.addAttribute("success", result.errorMessage != null ? result.errorMessage :
                    "Se envió un correo con el link para recuperar su contraseña");
                model.addAttribute("usuario", new Usuario());
                return "login";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Error al procesar la solicitud");
            }
            
        } catch(Exception ex){
            result.correct = false;
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.status = 500;
            model.addAttribute("error", result.errorMessage);
        }
    
        return "forgot";    
    }
    
    // ==================== RESTABLECER CONTRASEÑA ====================
    
    @GetMapping("/reset")
    public String reseteContrasena(@RequestParam String token, Model model){
        model.addAttribute("token", token);
        return "reset";   
    }
    
    @PostMapping("/reset")
    public String resetearContrasena(@RequestParam String token,
                                     @RequestParam String newPassword,
                                     Model model){
        Result result = new Result();
                        RestTemplate restTemplate = new RestTemplate();

        try{
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/reset?token=" + token + "&newPassword=" + newPassword,
                HttpMethod.POST,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<Result>(){}
            );
        
            result = response.getBody();
        
            if(response.getStatusCode().is2xxSuccessful() && result != null){
                model.addAttribute("success", result.errorMessage != null ? result.errorMessage :
                    "Contraseña restablecida correctamente");
                model.addAttribute("usuario", new Usuario());
                return "login";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Token inválido");
            }
        
        } catch(Exception ex){
            result.correct = false;
            result.status = 500;
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            model.addAttribute("error", result.errorMessage);
        }
        
        model.addAttribute("token", token);
        return "reset";
    }
    
    // ==================== CAMBIAR CONTRASEÑA ====================
    
    @GetMapping("/change-password")
    public String cambioContrasena(HttpSession session, Model model){
        String username = (String) session.getAttribute("username");
        if (username == null) {
            model.addAttribute("error", "Debes iniciar sesión primero");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }
        model.addAttribute("username", username);
        return "change-password"; 
    }
    
    @PostMapping("/change-password")
    public String cambiarContrasena(@RequestParam String newPassword,
                                    HttpSession session,
                                    Model model){
        Result result = new Result();
                        RestTemplate restTemplate = new RestTemplate();

        try{
            String username = (String) session.getAttribute("username");
            if(username == null){
                model.addAttribute("error", "Debes iniciar sesión primero");
                model.addAttribute("usuario", new Usuario());
                return "login";
            }
        
            String token = (String) session.getAttribute("token");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            
            HttpEntity<?> request = new HttpEntity<>(headers);
            
            ResponseEntity<Result> response = restTemplate.exchange(
                "http://localhost:8080/api/auth/change-password?username=" + username + 
                "&newPassword=" + newPassword,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<Result>(){}
            );
            
            result = response.getBody();
            
            if(response.getStatusCode().is2xxSuccessful() && result != null){
                model.addAttribute("success", result.errorMessage != null ? result.errorMessage : 
                    "Contraseña cambiada exitosamente");
                model.addAttribute("username", username);
                return "dashboard";
            } else {
                model.addAttribute("error", result != null ? result.errorMessage : "Error al cambiar contraseña");
            }
        
        } catch(Exception ex){
            result.correct = false;
            result.ex = ex;
            result.errorMessage = ex.getLocalizedMessage();
            result.status = 500;
            model.addAttribute("error", result.errorMessage);
        }
        
        model.addAttribute("username", session.getAttribute("username"));
        return "change-password";
    }
    
    // ==================== LOGOUT ====================
    
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session, Model model){
        session.invalidate();
        model.addAttribute("success", "Sesión cerrada exitosamente");
        model.addAttribute("usuario", new Usuario());
        return "login";
    }
}
