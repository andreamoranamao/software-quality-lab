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
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenUsernameEndsWithUnderscore() {
        // TODO: username "nombrevalido_"
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validaciones de Nombre ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenFirstNameTooShort() {
        // TODO: firstName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenFirstNameContainsNumbers() {
        // TODO: firstName como "Juan5"
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenLastNameTooShort() {
        // TODO: lastName de 1 carácter
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenLastNameContainsNumbers() {
        // TODO: lastName como "Perez2"
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validaciones de Age ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenAgeIsExactlyTwelve() {
        // TODO: age = 12 — caso límite (boundary): debe ser MAYOR a 12, no mayor o igual
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenAgeIsBelowTwelve() {
        // TODO: age = 5
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenAgeExceedsMaximum() {
        // TODO: age = 121 — excede el máximo permitido de 120
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validaciones de Phone ───────────────────────────────────────────────

    @Test
    void shouldThrowWhenPhoneHasWrongLength() {
        // TODO: phone con 9 u 11 dígitos
        // TODO: assertThrows InvalidUserDataException
    }

    @Test
    void shouldThrowWhenPhoneContainsLetters() {
        // TODO: phone como "123456789a"
        // TODO: assertThrows InvalidUserDataException
    }

    // ─── Validación de Email ──────────────────────────────────────────────────

    @Test
    void shouldThrowWhenEmailIsInvalid() {
        // TODO: mockear emailValidatorService.isValid(anyString()) para que regrese false
        // TODO: assertThrows InvalidUserDataException
        // TODO: verificar que emailValidatorService.isValid fue llamado (verify)
    }

    // ─── Unicidad de Username ─────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        // TODO: mockear emailValidatorService.isValid para que regrese true
        // TODO: mockear userRepository.existsByUsername para que regrese true
        // TODO: assertThrows DuplicateUsernameException
        // TODO: verificar que userRepository.save NUNCA fue llamado (verify never)
    }

    // ─── Not found ───────────────────────────────────────────────────────────

    @Test
    void shouldThrowWhenUserNotFound() {
        // TODO: mockear userRepository.findById para que regrese Optional.empty()
        // TODO: assertThrows UserNotFoundException
    }

    @Test
    void shouldThrowWhenSuspendingAlreadySuspendedUser() {
        // TODO: mockear findById con un usuario SUSPENDED
        // TODO: assertThrows InvalidUserDataException
    }
}
