package aiman.projectbackend.entity_rev;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;

/** BREVE TEORIA SULLE ENTITA
 *
 * DEF: Entità è uno stato persitito nel database, quindi quando istanzio la classe X questta corrisponde a una riga
 * della tabella Y
 *
 * CARATTERISTICHE:
 * 1) Condizioni --> sicuramente ci deve essere l'annotazione @Entity per indicare che è un'entità, ci deve essere anche
 * un riferimento al id @id, un costruttore di default e i campi non devono avere la dicitura final per il lazy loading
 * 2) Entità hanno un ciclo di vita ovvero:
 * 2.1) New o Transient --> l'oggetto è stato creato ma non è associato al db
 * 2.2) Managed o persistent --> l'oggetto è associato a una sessione del db, quindi ogni modifica lo salvo al db
 * 2.3) Detached --> l'oggetto è presente in db ma la sessione è chiusa, no sincronizzazione delle modifiche
 * 2.4) Remove --> l'oggetto è stato eliminato dal db
 *
 * OSSERVAZIONI:
 * - I campi non devono avere final per due motivi --> (1) recupero riga nel db, creo istanza vuota e poi ci scrivo, se metto
 * final dopo la creazione non posso scrivere i miei dati (2) lazy loading, ovvero non vengono caricati subito tutto il db,
 * ma viene creato una versione leggera, quindi è necessario una manipolazione dell'oggetto che sarebbe assente se ci fosse final
 * - Costruttore vuote --> eseguo una query, il sistema non sa quali dati passo nel costruttore, quindi c'è una chiamata al costruttore
 * vuoto per creare oggetto in memoria e poi riempe l'ogetto con dati provenient dal db usando i setter
 */

/**
 * Mini glossario per il recap:
 * 1) @GeneratedValue(strategy = GenerationType.IDENTITY) --> fai un id autoincrementale
 * 2) @NotBlank(message = " ... ") --> gestisci la validazione dei dati in cui affermi che il campo non può essere vuoto
 * 3) @OneToOne --> relazione 1 a 1 tra due tabelle
 * 3.1) @OneToMany --> relazione 1 a n tra due tabelle
 * 3.2) @ManyToOne --> relazione n a 1 tra due tabelle
 * 4) @JoinColumn(name = " ...") --> qui indico la foreign key della relazione tra tabelle
 * 5) @JsonIgnore --> mi serve per gestire casistiche in cui ho un loop sul json
 * 6) @MappedBy --> mi serve per gestire la relazione biderezionale, dove la foreignkey è presente nella tabella con relazione many,
 * evito che JPA mi crea colonne ridondanti, posso applicare anche strategie tipo cascade, quindi se elimino X nella relazione 1 allora
 * ho effetti anche su Y nella relazione n
 * 7) @Size(min = ValoreMinimo, max = ValoreMassimo, message = " ...") --> campo di validazione sulla dimensione del valore inserito
 * 8) @Column(nullable = false, unique = true) --> Il campo deve essere unico (username univoco) e non null, quindi generi un errore nel db se gli faccio insert di un null
 * OSS: @Column(nullable = false) vs @NotBlank(message = " ... ")  --> not blank controllo input utente, invece l'altro gestisco caso in cui l'utente che ha accesso al db inserisca dato null
 * 9) @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) --> il campo viene registrato, ma non viene vista dall'utente quando recupero i dati (quindi non appare a json se faccio get utente)
 * 10) @Email -->  è un campo di validazione per verificare se è corretto il dominio e se abbia la @
 * 11) @NotNull --> gestisci la validazione dei dati in cui affermi che il campo non può essere vuoto
 * OSS: @Notnull vs NotBlank --> notnull è per i campi con valori numerici, invece notblank è per le stringhe
 * 12) @Inheritance(strategy = InheritanceType.JOINED) --> mi serve per gestire il concetto di ereditarietà tra le classi, ad esempio product che si specializza con component o tool
 * 13) @JsonTypeInfo --> qui gestisco il valore da restituire nel json per specificare la specializzazione, quindi il type
 * 14) @JsonSubTypes --> qui indico il nome del type e in quale classe prendo il valore
 */

// @Entity mi serve per indicare che questa classe è entity a JPA e quindi di mapparlo nella tabella
@Entity
// @Table serve per indicare il nome della tabella nel db associato, se non lo metto prende quello della classe
@Table(name = "products")


// Strategia di ereditarietà, quindi creo la classe padre (products) con campi comuni, poi ci aggiungo 2 classe figlie separate
// che specializzano la classe padre, quindi component o tools che aggiungono campi specifici (collegamento padre e figlie tramite id)
@Inheritance(strategy = InheritanceType.JOINED)

/** Recap teoria:
 * Durante la restituzione della lista prodotti il client deve sapere la specializzazione del prodotto, ovvero component o tool
 * - use = JsonTypeInfo.Id.NAME --> inidico il nome per distinguere i due tipi di prodotto
 * - include = JsonTypeInfo.As.PROPERTY --> aggiungo al json base la proprietà fisica per la distinzione del tipo prodotto
 * - property = "type" --> è il nome del campo che appare nel json per fare la distinzione sul tipo prodotto
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
/**
 * @JsonSubTypes.Type(value = Component.class, name = "component") --> specifico le sottoclassi e il nome del tipo prodotto che andrà nel type
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value = Component.class, name = "component"),
        @JsonSubTypes.Type(value = Tool.class, name = "tool")
})
// Teoria base --> qui è abstract perchè ovviamente il product è concreto in component o tool, quindi product è solo un modello base
// quindi nella creazione del prodotto non parto dal new Product(), ma da new Tool() o Component() perchè sto creando un prodotto
// con le informazioni complete, quindi non è solo product, ma product con 1 o 2 campi in più che lo completano
public abstract class Product {

    /**
     * Definisco i campi che andranno comporre il mio product:
     * - ID
     * - nome prodotto
     * - descrizione
     * - prezzo
     * - qty a magazzino o stock qty
     *
     * Poi oltre a queste in base alla specializzazione andranno aggiungersi altri campi:
     *
     * Tool:
     * - nome del brand
     * - se è elettrico
     *
     * Component:
     * - materiale
     * - diametro
     */


    // Definisco la chiave primaria
    @Id
    // Dico come viene generata questa chiave, IDENTITY --> serve per dire che la strategia è un id autoincrementato
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    private String name;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "La descrizione è obbligatoria")
    private String description;

    // Notnull è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotNull(message = "Il prezzo è obbligatorio")
    // @Positive campo di validazione per indicarli un valore maggiore di 0
    @Positive(message = "Il prezzo deve essere maggiore di zero")
    private Double price;

    // Notnull è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotNull(message = "La quantità in magazzino è obbligatoria")
    // Min è campo di validazione per dire che oltre a essere positivo la quantità minima è 0
    @Min(value = 0, message = "La quantità non può essere negativa")
    private Integer stockQuantity;

    // ManyToOne --> qui ho molti prodotti che appartengono a una categoria specifica
    @ManyToOne
    // JoinColum --> è la mia foreign key per stabilire la relazione
    @JoinColumn(name = "category_id")
    private Category category;

    // Questo costruttore vuoto perchè JPA lo usa per istanziare l'oggetto e recuperare i dati
    /** Teoria breve
     * Costruttore vuote --> eseguo una query, il sistema non sa quali dati passo nel costruttore, quindi c'è una chiamata al costruttore
     *  * vuoto per creare oggetto in memoria e poi riempe l'ogetto con dati provenient dal db usando i setter
     */
    public Product() {}

    // Questo costruttore mi serve per creare i miei oggetti Product
    public Product(String name, String description, Double price, Integer stockQuantity, Category category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
    }

    // Getter e setter servono a JPA per accedere ai campi privati in questa classe (incapsulamento, proteggo i miei campi da modifiche esterne)
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}