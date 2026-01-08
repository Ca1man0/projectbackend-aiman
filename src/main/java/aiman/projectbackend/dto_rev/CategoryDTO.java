package aiman.projectbackend.dto_rev;

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

public class CategoryDTO {
    // Identificativo univoco della categoria
    private Long id;
    // Nome della categoria
    private String name;

    // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
    // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
    public CategoryDTO() {}

    // Costruttore per la creazione del DTO nel service
    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getter e Setter per l'accesso ai campi privati. Anche se vedo che alcuni non sono usati, quando il controller riceverà
    // un JSON dal client userà questi metodi per iniettare i valori nel campo o leggere valori dell'oggetto e trasformarli in JSON da
    // far vedere al client (postman)

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}