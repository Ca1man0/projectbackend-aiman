package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.AddressDTO;
import aiman.projectbackend.dto_rev.UserDTO;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.repository_rev.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import aiman.projectbackend.entity_rev.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/** BREVE TEORIA DEL SERVICE
 * DEF: Nel service ho la mia logica di business, quindi qui mostro come deve funzionare la mia applicazione back-end
 *
 * CARATTERISTICHE:
 * 1) Risiede la logica di business, ad esempio il calcolo del prezzo totale dei ordini
 * 2) Ha il ruolo di intermediario tra il Controller (gestisce le richieste HTTP) e Repository (comunica con il DB)
 * 3) Disaccopia, quindi il Controller non deve sapere come sono processati i dati, deve solo delegare il servizio richiesto
 */

// @Service è un annotazione serve per dire a spring che ho a che fare con un service e dice anche di creare una singola
// istanza all'avvio di questa classe
@Service
public class UserService {
    // Indico le dipendenze che saranno cambiate solo all'interno di questa classe "private" e dopo che sono stati
    // inizializzate non cambiano più "final"
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    // Costruttore che inietta le repository, quindi spring crea le istanze delle repository e le inietta nel mio servizio
    // Passwordencoder (configurato in securityconfig) mi serve per hashare la password prima di salvarlo in db
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // Metodo 1 - logica di business --> recupero tutti gli utenti e li converto in DTO
    public List<UserDTO> getAllUsers() {
        /** Recapone:
         * fidAll() --> recupero tutti i prodotti nel db
         * stream() --> converto questa lista in uno stream per elaborarli
         * map(this::convertToDTO) --> prende lo user e lo passi come argomento a convertToDTO, map(user -> this.convertToDTO(user))
         * collect(Collectors.toList()) --> prendo l'output e lo metto in una lista
         */
        return userRepository
                .findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Metodo 2 - logica di business --> salvo l'utente creato
    public UserDTO saveUser(User user) {
        // Verifico se lo user ha la data di registrazione, altrimenti lo imposto in modo automatico
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDateTime.now());
        }
        // Qui avviene hashing della password in chairo prima di fare il save nel db
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Salviamo il dato nel db attraverso la repo
        User savedUser = userRepository.save(user);
        // Ritorno il dto dell'utente salvato
        return convertToDTO(savedUser);
    }

    // Metodo 3 - logica di business --> salvo l'utente creato con il ruolo ADMIN
    public UserDTO saveAdmin(User user) {
        // Identico al save user ma qui setto il ruolo ADMIN per gestire poi le autorizzazione sui iendpoint
        user.setRole(Role.ADMIN);
        // Verifico se lo user ha la data di registrazione, altrimenti lo imposto in modo automatico
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDateTime.now());
        }
        // Qui avviene hashing della password in chairo prima di fare il save nel db
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Salviamo il dato nel db attraverso la repo
        User savedUser = userRepository.save(user);
        // Ritorno il dto dell'utente salvato
        return convertToDTO(savedUser);
    }

    // Metodo 4 - logica di business --> salvo l'utente creato con il ruolo SUPERADMIN
    public UserDTO saveSuperAdmin(User user) {
        // Identico al save user ma qui setto il ruolo SUPERADMIN per gestire poi le autorizzazione sui iendpoint
        user.setRole(Role.SUPERADMIN);
        // Verifico se lo user ha la data di registrazione, altrimenti lo imposto in modo automatico
        if (user.getRegistrationDate() == null) {
            user.setRegistrationDate(LocalDateTime.now());
        }
        // Qui avviene hashing della password in chairo prima di fare il save nel db
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Salviamo il dato nel db attraverso la repo
        User savedUser = userRepository.save(user);
        // Ritorno il dto dell'utente salvato
        return convertToDTO(savedUser);
    }

    // Metodo 5 - logica di business --> faccio l'update dell'immagine
    public UserDTO updateProfileImage(Long userId, String imageUrl) {
        // Prima trovo id dell'utente, se non lo trovo lancio l'eccezione in cui dico che è impossibile aggiornare la foto perchè utente non trovato
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("non è stato possibile aggiornare l'utente con id " + userId + " perchè non è stato trovato"));
        // Salvo url dell'utente nei dati del user
        user.setProfileImageUrl(imageUrl);
        // Aggiorno il db cercando l'utente (facendo update visto che aggiorno un campo e non tutti)
        User updatedUser = userRepository.save(user);
        // Ritorno il dto dell'utente aggiornato
        return convertToDTO(updatedUser);
    }

    // Metodo 6 - logica di business --> dammi l'uetente già convertito in dto
    public UserDTO getUserDtoById(Long id) {
        // Hai trovato l'utent con id che ti passo? Sì allora me lo salvi come oggetto user nella variabile user altrimenti lanci l'eccezione
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("utente con id " + id + " non è stato trovato"));
        // Restituisci l'utente convertito in dto
        return convertToDTO(user);
    }
    // Metodo 7 - logica di business --> cancella l'utente in riferimento al id
    public void deleteUser(Long id) {
        // L'utente con id non esiste? bene, allora lancia l'eccezione
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("utente con id " + id + " non è stato trovato, perciò fallimento dell'eliminazione dell'utente");
        }
        // Cancello l'utente con quel id nel db
        userRepository.deleteById(id);
    }

    // Metodo 8 - logica di business --> cancella tutti gli utenti (può servire sia come logica di business in cui si vuole pulire il db
    // , ma anche per velocizzare i test durante la creazione degli utenti)
    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Metodo 9 e 10 - logica di business --> questi metodi servono a AutheService per accedere ai ruoili e password criptate da verificare in login
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("utente non trovato con id " + id));
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("utente con email " + email + " non è stato trovato"));
    }

    // Metodo 11 - logica di business --> conversione utente entity nel suo dto (privato perchè lo uso solo qui dentro il metodo)
    private UserDTO convertToDTO(User user) {
        // inizializzo address visto che update lo faccio dopo quando l'utente lo aggiorna
        AddressDTO addressDto = null;

        // se l'address non è null allora lo converto già in dto
        if (user.getAddress() != null) {
            addressDto = new AddressDTO(
                    user.getAddress().getId(),
                    user.getAddress().getStreet(),
                    user.getAddress().getCity(),
                    user.getAddress().getZipCode()
            );
        }

        // ritorno l'utente convertito in dto
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRegistrationDate(),
                user.getProfileImageUrl(),
                addressDto
        );
    }
}