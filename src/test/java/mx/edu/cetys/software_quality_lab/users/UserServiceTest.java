package mx.edu.cetys.software_quality_lab.users;

import mx.edu.cetys.software_quality_lab.users.exceptions.DuplicateUsernameException;
import mx.edu.cetys.software_quality_lab.users.exceptions.InvalidUserDataException;
import mx.edu.cetys.software_quality_lab.users.exceptions.UserNotFoundException;
import mx.edu.cetys.software_quality_lab.validators.EmailValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    // EmailValidatorService debe ser mockeado — en pruebas unitarias no probamos dependencias externas
    @Mock
    EmailValidatorService emailValidatorService;

    @InjectMocks
    UserService userService;
    // request válido para reusar
    private UserController.UserRequest validRequest() {
        return new UserController.UserRequest("juan4_dev", "Juan", "Perez", "6641234567", "juan4#gmail.com", 25);
    }

    // simular user guardado en bd
    private User savedUser(String username) {
        User u = new User(username, "Juan", "Perez", "6641234567", "juan4#gmail.com", 25);
        u.setId(1L);
        return u;
    }



    // ─── Caso exitoso ─────────────────────────────────────────────────────────

    @Test
    void shouldRegisterUserSuccessfully() {
        //arrange
        when(emailValidatorService.isValid(anyString())).thenReturn(true);
        when(userRepository.existsByUsername("juan4_dev")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser("juan4_dev"));

        var response = userService.registerUser(validRequest());

        assertNotNull(response.id());
        assertEquals("juan4_dev", response.username());
        assertEquals("ACTIVE", response.status());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldGetUserByIdSuccessfully() {
        //Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser("juan4_dev")));

        //Act
        var response = userService.getUserById(1L);

        //Assert
        assertEquals(1L, response.id());
        assertEquals("juan4_dev", response.username());
    }

    @Test
    void shouldSuspendActiveUserSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser("juan4_dev")));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = userService.suspendUser(1L);

        assertEquals("SUSPENDED", response.status());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─── Validaciones de Username ─────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameTooShort() {
        // TODO: construir request con username de 4 caracteres
        var request = new UserController.UserRequest("juan", "Juan", "Perez", "6641234567", " juan4 # gmail . com ", 25);
        //Assert
        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);
        });
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenUsernameTooLong() {
        // TODO: construir request con username de 21 caracteres
        var request = new UserController.UserRequest("juan_perez234ghtuj6okgkko", "Juan", "Perez", "6641234567", " juan4 # gmail . com ", 25);

        // TODO: assertThrows InvalidUserDataException
        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});

    }

    @Test
    void shouldThrowWhenUsernameHasInvalidChars() {
        // TODO: username con mayúsculas o caracteres especiales, ej. "User@Name"
        var request = new UserController.UserRequest("juan", "Juan", "Perez", "6641234567", "User@Name", 25);

        // TODO: assertThrows InvalidUserDataException
        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
    }

    @Test
    void shouldThrowWhenUsernameStartsWithUnderscore() {
        // TODO: username "_nombrevalido"
        var request =  new UserController.UserRequest("_nombrevalido", "Juan", "Perez", "6641234567", "juan4#gmail.com", 25);

        // TODO: assertThrows InvalidUserDataException
        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});

    }

    @Test
    void shouldThrowWhenUsernameEndsWithUnderscore() {
        // TODO: username "nombrevalido_"
        var request =  new UserController.UserRequest("nombrevalido_", "Juan", "Perez", "6641234567", "juan4#gmail.com", 25);

        // TODO: assertThrows InvalidUserDataException
        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});

    }

    // ─── Validaciones de Nombre ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenFirstNameTooShort() {
        // TODO: firstName de 1 carácter
        var request = new UserController.UserRequest("juan4_dev", "J", "Perez", "6641234567", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});

    }

    @Test
    void shouldThrowWhenFirstNameContainsNumbers() {
        // TODO: firstName como "Juan5"
        var request = new UserController.UserRequest("juan4_dev", "Juan5", "Perez", "6641234567", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenLastNameTooShort() {
        // TODO: lastName de 1 carácter
        var request = new UserController.UserRequest("juan4_dev", "Juan", "P", "6641234567", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenLastNameContainsNumbers() {
        // TODO: lastName como "Perez2"
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez2", "6641234567", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validaciones de Age ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAgeIsExactlyTwelve() {
        // TODO: age = 12 — caso límite (boundary): debe ser MAYOR a 12, no mayor o igual
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez", "6641234567", "juan4#gmail.com", 12);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenAgeIsBelowTwelve() {
        // TODO: age = 5
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez", "6641234567", "juan4#gmail.com", 5);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenAgeExceedsMaximum() {
        // TODO: age = 121 — excede el máximo permitido de 120
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez", "6641234567", "juan4#gmail.com", 121);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validaciones de Phone ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenPhoneHasWrongLength() {
        // TODO: phone con 9 u 11 dígitos
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez", "664123457", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenPhoneContainsLetters() {
        // TODO: phone como "123456789a"
        var request = new UserController.UserRequest("juan4_dev", "Juan", "Perez", "123456789a", "juan4#gmail.com", 25);

        assertThrows(InvalidUserDataException.class, () -> {userService.registerUser(request);});
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validación de Email ──────────────────────────────────────────────────

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        when(emailValidatorService.isValid(anyString())).thenReturn(false);
        var req = new UserController.UserRequest("validuser", "Juan", "Perez", "6641234567", "invalid@gmail.com", 25);
        assertThrows(InvalidUserDataException.class, () -> userService.registerUser(req));
        verify(emailValidatorService).isValid(anyString());
    }

    // ─── Unicidad de Username ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        when(emailValidatorService.isValid(anyString())).thenReturn(true);
        when(userRepository.existsByUsername("juan4_dev")).thenReturn(true);
        assertThrows(DuplicateUsernameException.class, () -> userService.registerUser(validRequest()));
        verify(userRepository, never()).save(any());
    }

    // ─── Not found ───────────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void shouldThrowWhenSuspendingAlreadySuspendedUser() {
        User u = savedUser("juan4_dev");
        u.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(u));
        assertThrows(InvalidUserDataException.class, () -> userService.suspendUser(1L));
    }
}
