package aiman.projectbackend.dto_rev;

import com.fasterxml.jackson.annotation.JsonInclude;

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

/** Spiegazione annotazione JsonInclude
 * Questo annotazione mi serve per escludere alcuni campi che non sono presenti in Component e viceversa con Tool
 * quindi:
 * Prodotti di tipo Component --> avranno brand e isElectric null, quindi non saranno visualizzati
 * Prodotti di tipo Tool --> avranno material e diameter  null, quindi non saranno visualizzati
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {
    // Qui indico tutte le variabili del prodotto comuni a Component e Tool
    // Quindi id, nome, descrizione, prezzo, qty, categoria e il tipo
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String categoryName;
    private String type;

    // Questi sono campi specifici di Component, quindi materiale e diametro
    private String material;
    private Double diameter;

    // Questi sono campi specifici di Tool, quindi nome brand e se è elettrico
    private String brand;
    private Boolean isElectric;

    // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
    // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
    public ProductDTO() {}

    // Costruttore per la creazione del DTO nel service
    public ProductDTO(Long id, String name, String description, Double price, Integer stockQuantity,
                      String categoryName, String type, String material, Double diameter,
                      String brand, Boolean isElectric) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryName = categoryName;
        this.type = type;
        this.material = material;
        this.diameter = diameter;
        this.brand = brand;
        this.isElectric = isElectric;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Double getDiameter() {
        return diameter;
    }

    public void setDiameter(Double diameter) {
        this.diameter = diameter;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Boolean getElectric() {
        return isElectric;
    }

    public void setElectric(Boolean electric) {
        isElectric = electric;
    }
}