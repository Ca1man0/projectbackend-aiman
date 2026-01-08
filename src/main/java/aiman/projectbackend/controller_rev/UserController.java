package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.UserDTO;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.service_rev.api_rev.CloudinaryService;
import aiman.projectbackend.service_rev.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    // Costruttore della classe che usa la logica del dependency injection,
    // ovvero inietta l'istanza UserService e CloudinaryService dove ho la mia logica di business che userò
    // Quando inietta cercherà la classe @Service di tipo UserService e CloudinaryService

    // Cloudinary mi è servito per gestire il caricamento della foto profilo

    public UserController(UserService userService, CloudinaryService cloudinaryService) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Utilità del endpoint? Estrazione elenco di tutti gli user iscritti
     * Metodo? GET
     * Url? /api/users
     */
    @GetMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // userService.getAllUsers()--> delego al service il recupero lista di tutti gli utenti
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Utilità del endpoint? Salvo / creo un nuovo utente
     * Metodo? POST
     * Url? /api/users
     */
    @PostMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<UserDTO> createUser(
            // @Valid --> mi serve per controllare la correttezza del dato (lo trovo nella classe Entity)
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @Valid @RequestBody User user) {
        // Restituisco il DTO creato, chiamo il service per la creazione e invia lo stato di creazione che dovrebbe essere il 201
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

    /**
     * Utilità del endpoint? Cerco lo specifico utente guardando id
     * Metodo? GET
     * Url? /api/users/{id}
     */
    @GetMapping("/{id}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero lo stesso utente e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> getUserById(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long id) {
        // Eseguo la ricerca dell'utente tramite id
        UserDTO user = userService.getUserDtoById(id);
        // Eseguo pure un controllo in caso non trovassi quell'utente
        if (user == null) {
            throw new RuntimeException("Utente con ID " + id + " non trovato");
        }
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // Restituisco l'utente trovato
        return ResponseEntity.ok(user);
    }

    /**
     * Utilità del endpoint? Cancello uno specifico utente
     * Metodo? DELETE
     * Url esempio? /api/users/{id}
     */
    @DeleteMapping("/{id}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteUser(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long id) {
        // Eseguo cancellazione dell'utente tramite id
        userService.deleteUser(id);
        // ResponseEntity.noContent() --> non restituisco nulla, ovvero lo stato è 204 no content
        // .build() --> noContent() restituisce un costruttore, questo metodo finalizzazione la costruzione e crea un oggetto ResponseEntity da fare return
        return ResponseEntity.noContent().build();
    }

    /**
     * Utilità del endpoint? Carico l'immagine profilo
     * Metodo? PATCH (e non PUT perchè è una modifica parziale)
     * Url esempio? /api/users/{id}/image
     */
    @PatchMapping("/{id}/profile-image")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero lo stesso utente e il superadmin
    @PreAuthorize("#id == authentication.principal.id or hasRole('SUPERADMIN')")
    public ResponseEntity<UserDTO> uploadProfileImage(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long id,
            // @RequestParam --> leggo il file passato, dopo il "?"
            // MultipartFile? --> rappresenta il file caricato e contiene anche dati del file
            // Gestisco anche le eccezioni di caricamento, lettura e scrittura dati
            @RequestParam("file") MultipartFile file) throws IOException, IOException {

        // Qui carico l'immagine su cloudinary
        String imageUrl = cloudinaryService.uploadImage(file);
        // Qui aggiorno i dati dell'utente riportando url dell'immagine
        UserDTO updatedUser = userService.updateProfileImage(id, imageUrl);

        // Ritorno l'utente aggiornato
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        return ResponseEntity.ok(updatedUser);
    }
}