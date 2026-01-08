package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.LoginDTO;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.exception_rev.UnauthorizedException;
import aiman.projectbackend.security_rev.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
// Qui mi gestisco l'autenticazione al livello di dati, ovvero prendo dati dell'utente confronto il db e verifico l'autenticazione e rilascio il token
public class AuthService {

    // @Autowired --> inietto le dipendenze
    @Autowired
    // Mi serve per la comunicazione con il db
    private UserService usersService;

    // @Autowired --> inietto le dipendenze
    @Autowired
    // Mi serve per gestire la validazione e creazione token
    private JWTTools jwtTools;

    // @Autowired --> inietto le dipendenze
    @Autowired
    // Mi serve per gestire la crittografia delle password
    private PasswordEncoder bcrypt;

    public String checkCredentialsAndGenerateToken(LoginDTO body) {

        // Verifico le credenziali, guardando nel db se esiste l'email (se no lo trovo il metodo findbyemail lancia eccezione notfound)
        User found = this.usersService.findByEmail(body.email());

        // Verifico se la password inserite e uguale alla password cifrata nel db
        // bcrypt converte in hash la password inserite e fa la comparazione con il db già con hash
        if (bcrypt.matches(body.password(), found.getPassword())) {
            // Se okay genero il token
            return jwtTools.createToken(found);
        } else {
            // Se non okay genero errore 401 di errore nelel credenziali
            throw new UnauthorizedException("Ce un errore nelle credenziali");
        }
    }
}