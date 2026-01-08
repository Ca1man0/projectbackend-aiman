package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.ProductDTO;
import aiman.projectbackend.entity_rev.Product;
import aiman.projectbackend.service_rev.ProductService;
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
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // Costruttore della classe che usa la logica del dependency injection,
    // ovvero inietta l'istanza ProductService dove ho la mia logica di business che userò
    // Quando inietta cercherà la classe @Service di tipo ProductService

    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    /**
     * Utilità del endpoint? Estrazione elenco di tutti i prodotti
     * Metodo? GET
     * Url? /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // productService.getAllProducts()--> delego al service il recupero lista di tutti i prodotti
        return ResponseEntity.ok(productService.getAllProducts());
    }

    /**
     * Utilità del endpoint? Salvo un nuovo prodotto
     * Metodo? POST
     * Url? /api/products
     */
    @PostMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<ProductDTO> addProduct(
            // @Valid --> mi serve per controllare la correttezza del dato (lo trovo nella classe Entity)
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @Valid @RequestBody Product product) {
        // Restituisco il DTO creato, chiamo il service per la creazione e invia lo stato di creazione che dovrebbe essere il 201
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    /**
     * Utilità del endpoint? Cancellazione prodotto tramite ID
     * Metodo? DELETE
     * Url? /api/products
     */
    @DeleteMapping("/{id}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<Void> deleteProduct(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long id) {
        // Eseguo cancellazione del prodotto tramite id
        productService.deleteProduct(id);
        // ResponseEntity.noContent() --> non restituisco nulla, ovvero lo stato è 204 no content
        // .build() --> noContent() restituisce un costruttore, questo metodo finalizzazione la costruzione e crea un oggetto ResponseEntity da fare return
        return ResponseEntity.noContent().build();
    }

    /**
     * Utilità del endpoint? Scrivo che prodotto voglio cercare e lo trovo
     * Metodo? GET
     * Url esempio? /api/products/search?name=tubo
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(
            // @RequestParam --> lego il parametro della string di query, dopo il "?"
            @RequestParam String name) {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // productService.searchProductsByName(name) --> delego la ricerca del prodotto attraverso il suo nome
        return ResponseEntity.ok(productService.searchProductsByName(name));
    }

    /**
     * Utilità del endpoint? Filtro i prodotti per il prezzo (indicando il prezzo minimo e massimo)
     * Metodo? GET
     * Url esempio? /api/products/filter?min=10&max=50
     */
    @GetMapping("/filter")
    public ResponseEntity<List<ProductDTO>> filter(
            // @RequestParam --> lego il parametro della string di query, dopo il "?"
            @RequestParam Double min,
            @RequestParam Double max) {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // productService.filterByPrice(min, max) --> delego il filtraggio dei prodotti per il prezzo indicando il prezzo minimo e massimo
        return ResponseEntity.ok(productService.filterByPrice(min, max));
    }

    /**
     * Utilità del endpoint? Filtro i prodotti disponibili
     * Metodo? GET
     * Url esempio? /api/products/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<ProductDTO>> getAvailable() {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // productService.getAvailableProducts()--> delego il filtraggio dei prodotti disponibili
        return ResponseEntity.ok(productService.getAvailableProducts());
    }
}