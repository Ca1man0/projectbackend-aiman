package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.LoginDTO;
import aiman.projectbackend.dto_rev.LoginRespDTO;
import aiman.projectbackend.dto_rev.UserDTO;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.service_rev.AuthService;
import aiman.projectbackend.service_rev.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/** BREVE TEORIA DEI CONTROLLER
 *
 * DEF: Mi permette di rappresentare un punto di ingresso nell'archittetura REST, ovvero serve per gestire
 * la comunicazione tra il mondo esterno (in questo caso un client come Postman) e la logica del mio server
 *
 * CARATTERISTICHE:
 * 1) Espone gli endpoint, ovvero mappa gli indirizzi URL a metodi specifici con il @RequestMapping
 * 2) Endpoint ricevono oggetti in input, ma restituisce un DTO (Data transfer object), permettendo di non
 *    esporre la struttura del database all'esterno, inoltre evito casi come quello di mostrare password
 * 3) Gestisce lo stato HTTP usando il ResponseEntity
 * 4) Estrazione dati dall'url PathVariable o corpo della richiesta RequestBody, poi gli valida
 *
 * OSSERVAZIONI:
 * Nelle architetture REST si usano i metodi per definire l'azione che voglio eseguire in quella risorse
 * 1) GET --> recupero l'informazione
 * 2) POST --> invio dati al server
 * 3) DELETE --> rimuove i dati o la risorsa
 * Esempi di stati: 200 (richiesta e andata a buon fine), 201 (risorse create con successo) e 204 (no content, ad
 * esempio nella cancellazione indica che l'operazione è riuscita, infatti non ho nulla da restituire)
 */

// Serve per indicare che la classe gestisce la richieste di tipo REST, quindi ogni metodo restituisce
// dati (JSON) nel corpo della risposta
@RestController

// Serve per definire la radice URL per tutti i metodi / endpoint di questa classe
@RequestMapping("/auth")
public class AuthController {

    // @Autowired --> faccio l'iniezione della dipendenza (quindi bean fa il collegamento)
    // Questo mi serve per gestire la logica di login
    @Autowired
    private AuthService authService;

    // @Autowired --> faccio l'iniezione della dipendenza (quindi bean fa il collegamento)
    // Questo mi serve per gestire la logica per salvare i nuovi utenti nel db
    @Autowired
    private UserService usersService;

    /**
     * Utilità del endpoint? Qui faccio il login
     * Metodo? POST
     * Url? /register/admin
     */
    @PostMapping("/login")
    public LoginRespDTO login(
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @RequestBody LoginDTO body) {
        // Chiamo il service per verificare le credenziali. Se sono corrette, restituisco il Token (LoginRespDTO).
        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    /**
     * Utilità del endpoint? Registrazione del utente
     * Metodo? POST
     * Url? /register/admin
     */
    // Oserrvazione --> riporto il body del
    @PostMapping("/register")
    // Se non ho avuto nessun errore manda non il 200 ma il 201 che indica la creazione
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO createUser(
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @RequestBody
            // @Validated --> sfrutto i controlli validazione nella classe user
            @Validated User body, BindingResult validationResult) {

        // Qui raccolgo tutti i possibili errori
        if (validationResult.hasErrors()) {
            String messages = validationResult.getAllErrors()
                    // .stream --> trasformo la mia lista di errori un un flusso per lavorarci
                    .stream()
                    // .map --> prendo l'oggetto ovvvero l'errore e chiedo il messaggio di default e lo faccio per tutto lo stream
                    .map(objectError -> objectError.getDefaultMessage())
                    // .collect --> metto tutto insieme con il separatore virgola
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Errore raccolti " + messages);
        }
        // Salvo l'utente di ruolo user usando il service
        return this.usersService.saveUser(body);
    }


    /**
     * Utilità del endpoint? Registrazione del admin
     * Metodo? POST
     * Url? /register/admin
     */
    @PostMapping("/register/admin")
    // Se non ho avuto nessun errore manda non il 200 ma il 201 che indica la creazione
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerAdmin(
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @RequestBody
            // @Validated --> sfrutto i controlli validazione nella classe user
            @Validated User body, BindingResult validationResult) {
        // Qui faccio un controllo semplificato, tanto l'admin ha accesso ai messaggi di errore
        if (validationResult.hasErrors()) {
            throw new RuntimeException("Errore nel payload " + validationResult.getAllErrors());
        }
        // Salvo l'utente di ruolo admin usando il service (service qui assoccia il ruolo speciale)
        return this.usersService.saveAdmin(body);
    }

    /**
     * Utilità del endpoint? Registrazione del superadmin
     * Metodo? POST
     * Url? /register/superadmin
     */
    @PostMapping("/register/superadmin")
    // Se non ho avuto nessun errore manda non il 200 ma il 201 che indica la creazione
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerSuperAdmin(
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @RequestBody
            // @Validated --> sfrutto i controlli validazione nella classe user
            @Validated User body, BindingResult validationResult) {

        // Qui faccio un controllo semplificato, tanto l'admin ha accesso ai messaggi di errore
        if (validationResult.hasErrors()) {
            throw new RuntimeException("Errore nel payload " + validationResult.getAllErrors());
        }
        // Salvo l'utente di ruolo superadmin usando il service (service qui assoccia il ruolo speciale)
        return this.usersService.saveSuperAdmin(body);
    }
}