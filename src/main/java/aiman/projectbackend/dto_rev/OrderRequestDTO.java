package aiman.projectbackend.dto_rev;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/** BREVE TEORIA DEI DTO
 *
 * DEF: DTO (Data transfer object) è un oggetto che trasferisce i dati tra diversi layer dell'applicazione, ad esempio Controller --> Service
 *
 * CARATTERISTICHE:
 * 1) DTO non sono mappati direttamente sulle tabelle del database e non sono entità
 * 2) Con i DTO decido quali dati esporre, infatti risulta rischioso esporre le entità del database tramite API REST perchè
 * potrei avere campi sensibili o dettagli tecnici da non far vedere
 * 3) DTO evita che API sia legato strettamente allo schema del database, quindi se cambio le colonne della tabella API continuerà a funzionare
 * 4) DTO mi permettono di fare validazione come il @Notnull o @Size prima che arrivino al service
 */

public class OrderRequestDTO {
    // Id della richiesta d'ordine + controllo presenza id utente
    @NotNull(message = "id utente è obbligatorio")
    private Long userId;

    // Lista prodotti + controllo presenza di almeno un prodotto + validazione di ciascun prodotto
    @NotEmpty(message = "ordine deve contenere almeno un prodotto")
    private List<@Valid OrderItemRequestDTO> items;

    // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
    // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
    public OrderRequestDTO() {}

    // Getter e Setter per l'accesso ai campi privati. Anche se vedo che alcuni non sono usati, quando il controller riceverà
    // un JSON dal client userà questi metodi per iniettare i valori nel campo o leggere valori dell'oggetto e trasformarli in JSON da
    // far vedere al client (postman)

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    /** Breve spiegazione di questo DTO nidificato
     * Qui ho costruito un DTO nidificato che mi serve per rappresentare la singola riga dell'ordine
     * (ovvero che tipo di prodotto e qty) all'interno dell'ordine totale. Quindi avrò id del user e poi una
     * lista di items in cui indico id del prdotto e la loro qty
     *
     * Osservazione:
     * Static --> inteso che posso tranquillamente creare questa classe nidificata senza creare
     * istanza della classe esterna
     */
    public static class OrderItemRequestDTO {
        // Id del prodotto + verifica della sua presenza
        @NotNull(message = "id prodotto è necessario")
        private Long productId;

        // Indicazione qty con due controlli ovvero la qty minima e la presenza del dato obbligatorio
        @NotNull(message = "qty è necessaria")
        @Min(value = 1, message = "qty minima è >= 1")
        private Integer quantity;

        // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
        // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
        public OrderItemRequestDTO() {}

        // Getter e Setter per l'accesso ai campi privati. Anche se vedo che alcuni non sono usati, quando il controller riceverà
        // un JSON dal client userà questi metodi per iniettare i valori nel campo o leggere valori dell'oggetto e trasformarli in JSON da
        // far vedere al client (postman)

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }
    }
}