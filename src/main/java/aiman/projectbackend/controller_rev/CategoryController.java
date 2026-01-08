package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.CategoryDTO;
import aiman.projectbackend.entity_rev.Category;
import aiman.projectbackend.service_rev.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/api/categories")
public class CategoryController {

    /** Breve spiegazione sintassi (recapone):
     *  private (significa che solo in questa specifica classe posso modificare questa variabile),
     *  final  (significa che lo modifico una sola volta, ovvero quando lo assegno non lo modifico più,
     *          questo mi serve per evitare errori di sostituzione durante l'esecuzione del programma)
     *  Xxxx (indico il tipo di oggetto che può starci)
     *  yyyy (indico il nome della variabile)
     */

    private final CategoryService categoryService;

    // Costruttore della classe che usa la logica del dependency injection,
    // ovvero inietta l'istanza categoryService dove ho la mia logica di business che userò
    // Quando inietta cercherà la classe @Service di tipo CategoryService

    /** Breve spiegazione sintassi (recapone):
     * funzione publica (public disponibile a tutte le parti del programma) caratterizzato
     * da un nome della funzione XXXController in cui gli passo un oggetto di tipo Y. Dentro
     * poi assegno alla mia variabile private il valore che gli ho passato esternamente, quelle nelle parentesi tonde.
     */

    public CategoryController(CategoryService categoryService) {

        this.categoryService = categoryService;
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
     * Utilità del endpoint? Estrazione elenco di tutte le categorie
     * Metodo? GET
     * Url? /api/categories
     */
    @GetMapping
    // Nel contenitore della risposta avrò una lista di oggetti di tipo CategoryDTO
    public ResponseEntity<List<CategoryDTO>> getAll() {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // categoryService.getAllCategories --> delego al service il recupero lista categorie
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * Utilità del endpoint? Creazione di una nuova categoria
     * Metodo? POST
     * Url? /api/categories
     */
    @PostMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    // Nel contenitore della risposta avrò un oggetto di tipo CategoryDTO
    public ResponseEntity<CategoryDTO> create(
            // @Valid --> mi serve per controllare la correttezza del dato (lo trovo nella classe Entity)
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @Valid @RequestBody Category category) {
        // Restituisco il DTO creato, chiamo il service per il salvataggio e invia lo stato di creazione che dovrebbe essere il 201
        return new ResponseEntity<>(categoryService.saveCategory(category), HttpStatus.CREATED);
    }

    /**
     * Utilità del endpoint? Cancellazione categoria tramite ID
     * Metodo? DELETE
     * Url? /api/categories
     */
    @DeleteMapping("/{id}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> delete( // <Void> --> perchè non ho la necessità di restituire dati visto che sto cancellando qualcosa
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long id) {
        // Eseguo cancellazione della categoria tramite id
        categoryService.deleteCategory(id);
        // ResponseEntity.noContent() --> non restituisco nulla, ovvero lo stato è 204 no content
        // .build() --> noContent() restituisce un costruttore, questo metodo finalizzazione la costruzione e crea un oggetto ResponseEntity da fare return
        return ResponseEntity.noContent().build();
    }
}