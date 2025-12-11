package com.tareasCliente.views.Controller;

import com.tareasCliente.views.ML.Result;
import com.tareasCliente.views.ML.Tarea;
import com.tareasCliente.views.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import java.util.List;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tareas")
public class TareasController {

    @GetMapping
    public String listarTareas(Model model, HttpSession session) {

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

            ResponseEntity<Result<Tarea>> response = restTemplate.exchange(
                    "http://localhost:8080/api/tareas/listadoTareas/" + idUsuario,
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<Result<Tarea>>() {}
            );

            Result<Tarea> result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null && result.objects != null) {
                List<Tarea> tareas = result.objects;
                model.addAttribute("tareas", tareas);
            } else {
                model.addAttribute("tareas", List.of());
            }

        } catch (HttpClientErrorException ex) {
            // Si es 404, significa que no hay tareas (no es un error)
            if (ex.getStatusCode().value() == 404) {
                model.addAttribute("tareas", List.of());
            } else {
                // Otros errores 4xx
                model.addAttribute("error", "Error al cargar las tareas");
                model.addAttribute("tareas", List.of());
            }
            
        } catch (Exception ex) {
            // Errores de conexión u otros
            model.addAttribute("error", "Error de conexión con el servidor");
            model.addAttribute("tareas", List.of());
        }

        model.addAttribute("username", username);
        return "tareas/lista";
    }

    @GetMapping("/nueva")
    public String mostrarFormulario(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            model.addAttribute("error", "Debes iniciar sesión primero");
            model.addAttribute("usuario", new Usuario());
            return "login";
        }
        model.addAttribute("tarea", new Tarea());
        model.addAttribute("estados", Tarea.EstadoTarea.values());
        model.addAttribute("username", username);

        return "tareas/form";
    }

    @PostMapping("/nueva")
    public String crearTareas(@ModelAttribute Tarea tarea, 
                             HttpSession session, 
                             RedirectAttributes redirectAttributes) {
        RestTemplate restTemplate = new RestTemplate();
        String username = (String) session.getAttribute("username");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (username == null || idUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión primero");
            return "redirect:/auth/login";
        }

        try {
            String token = (String) session.getAttribute("token");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<Tarea> request = new HttpEntity<>(tarea, headers);

            ResponseEntity<Result<Tarea>> response = restTemplate.exchange(
                    "http://localhost:8080/api/tareas/usuario/" + idUsuario,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Result<Tarea>>() {}
            );

            Result<Tarea> result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                redirectAttributes.addFlashAttribute("success", "Tarea creada exitosamente");
                return "redirect:/tareas";
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    result != null ? result.errorMessage : "Error al crear la tarea");
                return "redirect:/tareas/nueva";
            }

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la tarea: " + ex.getMessage());
            return "redirect:/tareas/nueva";
        }
    }

    @GetMapping("/editar/{idTarea}")
public String mostrarFormularioEditar(@PathVariable int idTarea, 
                                     Model model, 
                                     HttpSession session) {

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

        ResponseEntity<Result<Tarea>> response = restTemplate.exchange(
                "http://localhost:8080/api/tareas/usuario/" + idUsuario + "/" + idTarea,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<Result<Tarea>>() {}
        );

        Result<Tarea> result = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && result != null && result.object != null) {
            Tarea tarea = result.object;
                       
            model.addAttribute("tarea", tarea);
            model.addAttribute("estados", Tarea.EstadoTarea.values());
            model.addAttribute("editar", true);
            model.addAttribute("username", username);
            return "tareas/form";
        } else {
            model.addAttribute("error", "Tarea no encontrada");
            return "redirect:/tareas";
        }
    } catch (Exception ex) {
        model.addAttribute("error", "Error al cargar la tarea: " + ex.getMessage());
        return "redirect:/tareas";
    }
}

    @PostMapping("/editar/{idTarea}")
public String actualizarTarea(@PathVariable int idTarea, 
                              @ModelAttribute Tarea tarea, 
                              HttpSession session, 
                              RedirectAttributes redirectAttributes) {

    RestTemplate restTemplate = new RestTemplate();

    String username = (String) session.getAttribute("username");
    Integer idUsuario = (Integer) session.getAttribute("idUsuario");

    if (username == null || idUsuario == null) {
        redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión primero");
        return "redirect:/auth/login";
    }

    try {
        String token = (String) session.getAttribute("token");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        tarea.setIdTarea(idTarea);

        HttpEntity<Tarea> request = new HttpEntity<>(tarea, headers);

        ResponseEntity<Result<Tarea>> response = restTemplate.exchange(
                "http://localhost:8080/api/tareas/usuario/" + idUsuario + "/" + idTarea,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<Result<Tarea>>() {}
        );

        Result<Tarea> result = response.getBody();

        if (response.getStatusCode().is2xxSuccessful() && result != null) {
            redirectAttributes.addFlashAttribute("success", "Tarea actualizada exitosamente");
            return "redirect:/tareas";
        } else {
            redirectAttributes.addFlashAttribute("error", 
                result != null ? result.errorMessage : "Error al actualizar tarea");
            return "redirect:/tareas/editar/" + idTarea;
        }

    } catch (Exception ex) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar la tarea: " + ex.getMessage());
        return "redirect:/tareas/editar/" + idTarea;
    }
}

    @PostMapping("/eliminar/{idTarea}")
    public String eliminarTarea(@PathVariable int idTarea,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        RestTemplate restTemplate = new RestTemplate();

        String username = (String) session.getAttribute("username");
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        if (username == null || idUsuario == null) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión primero");
            return "redirect:/auth/login";
        }

        try {
            String token = (String) session.getAttribute("token");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Result<Tarea>> response = restTemplate.exchange(
                    "http://localhost:8080/api/tareas/usuario/eliminacion/" + idUsuario + "/" + idTarea,
                    HttpMethod.DELETE,
                    request,
                    new ParameterizedTypeReference<Result<Tarea>>() {}
            );

            Result<Tarea> result = response.getBody();

            if (response.getStatusCode().is2xxSuccessful() && result != null) {
                redirectAttributes.addFlashAttribute("success", "Tarea eliminada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    result != null ? result.errorMessage : "Error al eliminar la tarea");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la tarea: " + e.getMessage());
        }

        return "redirect:/tareas";
    }
}