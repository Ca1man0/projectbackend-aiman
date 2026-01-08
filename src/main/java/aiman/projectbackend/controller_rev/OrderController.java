package aiman.projectbackend.controller_rev;

import aiman.projectbackend.dto_rev.OrderRequestDTO;
import aiman.projectbackend.entity_rev.Order;
import aiman.projectbackend.service_rev.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    // Costruttore della classe che usa la logica del dependency injection,
    // ovvero inietta l'istanza OrderService dove ho la mia logica di business che userò
    // Quando inietta cercherà la classe @Service di tipo OrderService
    public OrderController(OrderService orderService) {

        this.orderService = orderService;
    }

    /**
     * Utilità del endpoint? Creazione di un nuovo ordine
     * Metodo? POST
     * Url? /api/orders
     */
    @PostMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero lo stesso utente e il superadmin
    @PreAuthorize("#orderDto.userId == authentication.principal.id or hasRole('SUPERADMIN')")
    public Order placeOrder(
            // @Valid --> mi serve per controllare la correttezza del dato (lo trovo nella classe Entity)
            // @RequestBody --> trasforma il corpo della richiesta JSON in un oggetto XXX
            @Valid @RequestBody OrderRequestDTO orderDto) {
        // Chiamo il service per la creazione dell'ordine
        return orderService.createOrderFromDto(orderDto);
    }

    /**
     * Utilità del endpoint? Visualizzo lista ordini totali
     * Metodo? GET
     * Url? /api/orders
     */
    @GetMapping
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public List<Order> getAllOrders() {
        // Chiamo il service per restituire una lista di ordini json
        return orderService.getAllOrders();
    }

    /**
     * Utilità del endpoint? Calcolo il totale della spesa del singolo utente, specificando id
     * Metodo? GET
     * Url? /api/orders/user/{userId}/total
     */
    @GetMapping("/user/{userId}/total")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, admin, superadmin e lo stesso utente
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Double> getTotalSpending(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long userId) {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // orderService.getUserTotalSpending(userId) --> delego il calcolo della spesa ordine del singolo utente
        return ResponseEntity.ok(orderService.getUserTotalSpending(userId));
    }

    /**
     * Utilità del endpoint? Visualizzo lista ordini totali dello specifico utente (id)
     * Metodo? GET
     * Url? /api/orders/user/{userId}
     */
    @GetMapping("/user/{userId}")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, admin, superadmin e lo stesso utente
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Order>> getOrdersByUser(
            // @PathVariable --> prendo il valore dell'url ovvero ID e lo passo al metodo
            @PathVariable Long userId) {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // orderService.getOrdersByUser(userId) --> delego la ricerca dei ordini del singolo utente
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    /**
     * Utilità del endpoint? Filtro gli ordini con uno specifico status
     * Metodo? GET
     * Url di esempio? /api/orders/status?status=PENDING
     */
    @GetMapping("/status")
    // Stabilisco le autorizzazioni su chi può usare questo endpoint, ovvero l'admin e il superadmin
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    public ResponseEntity<List<Order>> getByStatus(
            // @RequestParam --> lego il parametro della string di query, dopo il "?"
            @RequestParam String status) {
        // ResponseEntity.ok --> metodo che imposta come risposta status 200 (ok)
        // orderService.getOrdersByStatus(status) --> delego il filtraggio degli ordini con uno specifico status
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }
}