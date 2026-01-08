package aiman.projectbackend.entity_rev;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Importa questo
import java.util.List;

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
@Table(name = "categories")
public class Category {

    /**
     * Definisco i campi che andranno comporre il mio category:
     * - ID
     * - nome categoria
     */

    // Definisco la chiave primaria
    @Id
    // Dico come viene generata questa chiave, IDENTITY --> serve per dire che la strategia è un id autoincrementato
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "il nome della categoria è obbligatorio")
    private String name;

    // OneToMany --> stabilisco la relazione, dicendo che una categoria può avere n prodotti
    @OneToMany(mappedBy = "category")
    // JsonIgnore --> mi serve per evitare di avere json infiniti, infatti ad esempio se user ha riferimento address e address ha riferimento a user
    @JsonIgnore
    private List<Product> products;

    // Questo costruttore vuoto perchè JPA lo usa per istanziare l'oggetto e recuperare i dati
    /** Teoria breve
     * Costruttore vuote --> eseguo una query, il sistema non sa quali dati passo nel costruttore, quindi c'è una chiamata al costruttore
     *  * vuoto per creare oggetto in memoria e poi riempe l'ogetto con dati provenient dal db usando i setter
     */
    public Category() {}

    // Questo costruttore mi serve per creare i miei oggetti categories
    public Category(String name) {
        this.name = name;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}