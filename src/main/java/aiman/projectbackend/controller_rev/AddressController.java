package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.AddressDTO;
import aiman.projectbackend.entity_rev.Address;
import aiman.projectbackend.service_rev.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/api/addresses")
public class AddressController {

    /** Breve spiegazione sintassi (recapone):
     *  private (significa che solo in questa specifica classe posso modificare questa variabile),
     *  final  (significa che lo modifico una sola volta, ovvero quando lo assegno non lo modifico più,
     *          questo mi serve per evitare errori di sostituzione durante l'esecuzione del programma)
     *  Xxxx (indico il tipo di oggetto che può starci)
     *  yyyy (indico il nome della variabile)
     */

    private final AddressService addressService;

    // Costruttore della classe che usa la logica del dependency injection,
    // ovvero inietta l'istanza AddressService dove ho la mia logica di business che userò
    // Quando inietta cercherà la classe @Service di tipo AddressService

    /** Breve spiegazione sintassi (recapone):
     * funzione publica (public disponibile a tutte le parti del programma) caratterizzato
     * da un nome della funzione XXXController in cui gli passo un oggetto di tipo Y. Dentro
     * poi assegno alla mia variabile private il valore che gli ho passato esternamente, quelle nelle parentesi tonde.
     */

    public AddressController(AddressService addressService) {

        this.addressService = addressService;
    }

    /** Breve spiegazione sintassi (recapone):
     * <> (generics) --> definisco il contenuto dell'oggetto
     * () --> definisco l'azione, passando i dati a un metodo/funzione
     * public tipo_di_ritorno<XXX> nomeMetodo
     *
     * ---------------------------------------------
     *
     * return new tipo_di_ritorno<> (yyy, ooo) --> invio il risultato, creando nuova istanza o oggetto della classe
     * tipo_di_ritorno dove mi prendi il tipo già dichiarato <> nella firma del metodo, trasformando i dati in JSON yyy
     * e comunicando al client lo stato HTTP 201, ooo
     */


    /**
     * Utilità del endpoint? Mi permette di aggiungere l'indirizzo all'utente specifico
     * Metodo? POST
     * Url? /api/addresses/user/{userId}
     */
    @PostMapping("/user/{userId}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero lo stesso utente e il superadmin
    @PreAuthorize("#userId == authentication.principal.id or hasRole('SUPERADMIN')")
    public ResponseEntity<AddressDTO> addAddress(
            // @Valid --> mi serve per controllare la correttezza del dato (lo trovo nella classe Entity)
            // @PathVariable --> estrago la variabile dinamica direttamente dall'url
            @Valid @PathVariable Long userId, // Estrago id dall'url, lo valido e lo metto in userId

            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @RequestBody Address address) { // Il client (postman) invia il testo formato JSON, qui lo trasformo in un oggetto Address per lavorarci sopra

        // Chiamo il servizio (che ho inietatto in precedenza) per delegare il salvataggio e l'associazione dell'address all'utente
        AddressDTO savedAddress = addressService.addAddressToUser(userId, address);
        // Restituisco il DTO creato e invia lo stato di creazione che dovrebbe essere il 201
        return new ResponseEntity<>(savedAddress, HttpStatus.CREATED);
    }
}