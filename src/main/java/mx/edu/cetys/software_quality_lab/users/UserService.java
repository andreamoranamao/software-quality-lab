package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final EmailValidatorService emailValidatorService;

    public UserService(UserRepository userRepository, EmailValidatorService emailValidatorService) {
        this.userRepository = userRepository;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * Registrar un nuevo usuario aplicando todas las reglas de negocio.
     *
     * Reglas a implementar (lanzar InvalidUserDataException a menos que se indique):
     *  1. Username  — entre 5 y 20 caracteres, solo letras minúsculas, dígitos y guion bajo (_),
     *                 NO debe comenzar ni terminar con guion bajo
     *  2. First name — entre 2 y 50 caracteres, solo letras (se permiten acentos: á, é, ñ, etc.)
     *  3. Last name  — entre 2 y 50 caracteres, solo letras (se permiten acentos)
     *  4. Age        — debe ser mayor a 12 y menor o igual a 120
     *  5. Phone      — exactamente 10 dígitos, sin letras ni símbolos
     *  6. Email      — delegar a emailValidatorService.isValid(email);
     *                  lanzar InvalidUserDataException si regresa false
     *  7. Unicidad del username — si userRepository.existsByUsername regresa true,
     *                             lanzar DuplicateUsernameException
     */
    UserController.UserResponse registerUser(UserController.UserRequest request) {
        log.info("Iniciando registro de usuario, username={}", request.username());

        if (request.username().isBlank() || request.username().length() < 5 || request.username().length() > 20) {
            throw new InvalidUserDataException("El username debe tener entre 5 y 20 caracteres");
        }

        if (request.username().startsWith("_") || request.username().endsWith("_")) {
            throw new InvalidUserDataException("El username no debe iniciar o terminar con _");
        }

        if (request.username().matches("[A-Z]")) {
            throw new InvalidUserDataException("El username solo debe contener minusculas, no mayusculas");
        }

        if (request.firstName().length() < 2 || request.firstName().length() > 50) {
            throw new InvalidUserDataException("El first name debe tener entre 2 y 50 caracteres");
        }

        if (request.lastName().length() < 2 || request.lastName().length() > 50) {
            throw new InvalidUserDataException("El last name debe tener entre 2 y 50 caracteres");
        }

        if (request.age() < 12 || request.age() > 120) {
            throw new InvalidUserDataException("El age debe tener entre 12 y 120");
        }

        if (request.phone().length() != 10) {
            throw new InvalidUserDataException("El phone debe se de 10 digitos");
        }

        if (!request.phone().matches("^\\d+$")) {
            throw new InvalidUserDataException("El phone no debe contener letras o caracteres especiales, solo números.");
        }

        if (!emailValidatorService.isValid(request.email())) {
            throw new InvalidUserDataException("El email no es valido");
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateUsernameException("Este nombre de usuario ya existe");
        }

        if (request.username().matches("^[a-z0-9_]*$")) {
            var newUser = userRepository.save(new User(request.username(), request.firstName(), request.lastName(), request.phone(), request.email(), request.age()));
            return mapToResponse(newUser);
        } else {
            throw new InvalidUserDataException("El username contiene caracteres invalidos");
        }
    }

    /**
     * Buscar un usuario por ID.
     * Lanzar UserNotFoundException (HTTP 404) si el usuario no existe.
     */
    UserController.UserResponse getUserById(Long id) {
        log.info("Buscando usuario por ID, id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        return mapToResponse(user);
    }

    /**
     * Suspender un usuario ACTIVO.
     * Lanzar UserNotFoundException si el usuario no existe.
     * Lanzar InvalidUserDataException si el usuario ya está SUSPENDED.
     */
    UserController.UserResponse suspendUser(Long id) {
        log.info("Suspendiendo usuario, id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        if(user.getStatus() != UserStatus.SUSPENDED) {
            user.setStatus(UserStatus.SUSPENDED);
        } else {
            throw new InvalidUserDataException("El usuario ya está suspendido");
        }

        var userWithUpdatedStatus = userRepository.save(user);

        return mapToResponse(userWithUpdatedStatus);
    }

    private UserController.UserResponse mapToResponse(User user) {
        return new UserController.UserResponse(user.getId(), user.getUsername(), user.getFirstName(), user.getLastName(), user.getPhone(), user.getEmail(), user.getAge(), user.getStatus().toString());
    }
}
