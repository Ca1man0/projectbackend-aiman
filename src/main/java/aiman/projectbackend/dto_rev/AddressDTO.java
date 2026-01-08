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

public class AddressDTO {
    // Identificativo univoco dell'indirizzo
    private Long id; // Aggiunto ID
    // Nome della via
    private String street;
    // Nome della città
    private String city;
    // Il CAP di riferimento
    private String zipCode;

    // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
    // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
    public AddressDTO() {}

    // Costruttore per la creazione del DTO nel service
    public AddressDTO(Long id, String street, String city, String zipCode) {
        this.id = id;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
    }

    // Getter e Setter per l'accesso ai campi privati. Anche se vedo che alcuni non sono usati, quando il controller riceverà
    // un JSON dal client userà questi metodi per iniettare i valori nel campo o leggere valori dell'oggetto e trasformarli in JSON da
    // far vedere al client (postman)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}