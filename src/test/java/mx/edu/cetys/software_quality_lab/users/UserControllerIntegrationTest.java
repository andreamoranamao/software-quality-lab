package mx.edu.cetys.software_quality_lab.users;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    // Limpiar la BD antes de cada prueba para garantizar un estado independiente
    @BeforeEach
    public void limpiarBD() {
        userRepository.deleteAll();
    }

    // ─── POST /users ──────────────────────────────────────────────────────────

    @Test void shouldCreateUserAndReturn201() throws Exception { // El email sigue el formato del EmailValidatorService: usuario#proveedor.dominio // TODO: realizar POST /users con el body anterior // TODO: andExpect status 201 // TODO: andExpect jsonPath("$.info") contiene "creado" o similar // TODO: andExpect jsonPath("$.response.user.username") == "juan4_dev" // TODO: andExpect jsonPath("$.response.user.status") == "ACTIVE"
         String body = """
                 { "username": "juan4_dev", "firstName": "Juan", "lastName": "Pérez", "phone": "6641234567", "email": "j4n#gmx.com", "age": 25 }""";
         mockMvc.perform( post("/users") .contentType(MediaType.APPLICATION_JSON) .content(body) )
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("$.info",containsString("Usuario registrado")))
                 .andExpect(jsonPath("$.response.user.username").value("juan4_dev"))
                 .andExpect(jsonPath("$.response.user.firstName").value("Juan"))
                 .andExpect(jsonPath("$.response.user.lastName").value("Pérez"))
                 .andExpect(jsonPath("$.response.user.phone").value("6641234567"))
                 .andExpect(jsonPath("$.response.user.email").value("j4n#gmx.com"))
                 .andExpect(jsonPath("$.response.user.age").value(25))
                 .andExpect(jsonPath("$.response.user.status").value("ACTIVE"))
                 .andExpect(jsonPath("$.error").isEmpty());  }


        @Test
        void shouldReturn400WhenUsernameIsTooShort() throws Exception {
            // TODO: body con username de 4 caracteres

        // TODO: realizar POST /users
        // TODO: andExpect status 400
        String body = """ 
                {"username": "juan", "firstName": "Juan", "lastName": "Pérez", "phone": "6641234567", "email": "juan4#gmail.com", "age": 25}""";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

        @Test
        void shouldReturn400WhenAgeIsExactlyTwelve() throws Exception {
        // TODO: body con age = 12 (caso límite — debe ser mayor a 12)
        // TODO: realizar POST /users
        // TODO: andExpect status 400
        String body = """ 
                 { "username": "juan4_dev", "firstName": "Juan", "lastName": "Pérez", "phone": "6641234567", "email": "juan4#gmail.com", "age": 12 }""";
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

@Test void shouldReturn400WhenPhoneIsInvalid() throws Exception {
    // TODO: body con phone = "123" (menos de 10 dígitos)
    // TODO: realizar POST /users
    // TODO: andExpect status 400
    String body = """ 
            {"username": "juan4_dev", "firstName": "Juan", "lastName": "Pérez", "phone": "123", "email": "juan4#gmail.com", "age": 25}""";
    mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest()); }

    @Test void shouldReturn400WhenEmailIsInvalid() throws Exception {
        // TODO: body con email en formato estándar "user@gmail.com" (no cumple las reglas del validador)
        // TODO: realizar POST /users
        // TODO: andExpect status 400
        String body = """ 
                {"username": "juan@gmail.com", "firstName": "Juan", "lastName": "Pérez", "phone": "6641234567", "email": "juan4#gmail.com", "age": 25} """;
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest()); }

    @Test
    void shouldReturn409WhenUsernameIsDuplicated() throws Exception {
        // TODO: guardar un usuario directamente via repository con el mismo username
        // TODO: realizar segundo POST /users con el mismo username
        // TODO: andExpect status 409
    }

    // ─── GET /users/{id} ─────────────────────────────────────────────────────

    @Test
    void shouldReturn200AndUserWhenFound() throws Exception {
        // TODO: guardar un usuario via repository, obtener su id generado
        // TODO: realizar GET /users/{id}
        // TODO: andExpect status 200
        // TODO: andExpect jsonPath campos coincidan con el usuario guardado
        User user = new User(
                "juan4_dev",
                "Juan",
                "Pérez",
                "6641234567",
                "j4n#gmx.com",
                25
        );

        User savedUser = userRepository.save(user);

        mockMvc.perform(get("/users/{id}", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.user.id").value(savedUser.getId()))
                .andExpect(jsonPath("$.response.user.username").value("juan4_dev"))
                .andExpect(jsonPath("$.response.user.status").value("ACTIVE"));

    }

    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        // TODO: realizar GET /users/9999 (id inexistente)
        // TODO: andExpect status 404
        mockMvc.perform(get("/users/{id}", 9999))
                .andExpect(status().isNotFound());
    }

    // ─── PATCH /users/{id}/suspend ────────────────────────────────────────────

    @Test
    void shouldSuspendUserAndReturn200() throws Exception {
        // TODO: guardar un usuario ACTIVE via repository
        // TODO: realizar PATCH /users/{id}/suspend
        // TODO: andExpect status 200
        // TODO: andExpect jsonPath("$.response.user.status") == "SUSPENDED"
        User user = new User(
                "juan4_dev",
                "Juan",
                "Pérez",
                "6641234567",
                "j4n#gmx.com",
                25
        );

        User savedUser = userRepository.save(user);

        mockMvc.perform(patch("/users/{id}/suspend", savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.user.status").value("SUSPENDED"));
    }

//    @Test
//    void shouldReturn400WhenSuspendingAlreadySuspendedUser() throws Exception {
//        // TODO: guardar un usuario con status SUSPENDED via repository
//        // TODO: realizar PATCH /users/{id}/suspend
//        // TODO: andExpect status 400
//
//    }
}
