package aiman.projectbackend.entity_rev;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
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
@Table(name = "users")
public class User implements UserDetails {

    /**
     * Definisco i campi che andranno comporre il mio user:
     * - ID
     * - Username
     * - Password
     * - Email
     * - Nome
     * - Cognome
     * - Data di regirstrazione
     * - Immagine profilo url
     */

    // Definisco la chiave primaria
    @Id
    // Dico come viene generata questa chiave, IDENTITY --> serve per dire che la strategia è un id autoincrementato
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "Lo username è obbligatorio")
    // Size è per la validazione dimensione del valore
    @Size(min = 4, max = 20, message = "Lo username deve essere tra 4 e 20 caratteri")
    // Il campo deve essere unico (username univoco) e non null, quindi generi un errore nel db se gli faccio insert di un null
    // OSS: @Column(nullable = false) vs @NotBlank(message = " ... ")  --> not blank controllo input utente, invece l'altro gestisco caso in cui l'utente che ha accesso al db inserisca dato null
    @Column(nullable = false, unique = true)
    private String username;

    // @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) mi permette di gestire il campo solo in scrittura, quindi la password la registro ma non faccio vedere come output o valore di ritorno all'utente per la sicurezza
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "La password è obbligatoria")
    // Size è per la validazione dimensione del valore
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    // Il campo deve essere non null, quindi generi un errore nel db se gli faccio insert di un null
    // OSS: @Column(nullable = false) vs @NotBlank(message = " ... ")  --> not blank controllo input utente, invece l'altro gestisco caso in cui l'utente che ha accesso al db inserisca dato null
    @Column(nullable = false)
    private String password;

    // @Email è un campo di validazione per verificare se è corretto il dominio e se abbia la @
    @Email(message = "Inserire un indirizzo email valido")
    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "L'email è obbligatoria")
    // Il campo deve essere unico (username univoco) e non null, quindi generi un errore nel db se gli faccio insert di un null
    // OSS: @Column(nullable = false) vs @NotBlank(message = " ... ")  --> not blank controllo input utente, invece l'altro gestisco caso in cui l'utente che ha accesso al db inserisca dato null
    @Column(nullable = false, unique = true)
    private String email;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "Nome è obbligatorio")
    private String firstName;

    // NotBlank è per la validaizone, impedisce che il campo sia vuoto con anche il msg di errore
    @NotBlank(message = "Cognome è obbligatorio")
    private String lastName;

    private LocalDateTime registrationDate;
    private String profileImageUrl;

    // Salvo  questo valore ENUM come stringa nel ruolo
    @Enumerated(EnumType.STRING)
    private Role role;

    // Relazione 1 a 1, quindi l'utente ha 1 solo address e cascade, quindi se elimino utente elimino il suo indirizzo nell'altra tabella
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Address address;

    // Relazioen 1 a n, quindi l'utente ha n ordini, anche qui strategia cascade quindi se elimino l'utente elimino anche i suoi ordini nell'altra tabella
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    // JsonIgnore --> mi serve per evitare di avere json infiniti, infatti ad esempio se user ha riferimento address e address ha riferimento a user
    // ottengo come risultato nel output del client su postman un json lunghissimo a causa di questo loop infinito
    @JsonIgnore
    private List<Order> orders;

    // Questo costruttore vuoto perchè JPA lo usa per istanziare l'oggetto e recuperare i dati
    /** Teoria breve
     * Costruttore vuote --> eseguo una query, il sistema non sa quali dati passo nel costruttore, quindi c'è una chiamata al costruttore
     *  * vuoto per creare oggetto in memoria e poi riempe l'ogetto con dati provenient dal db usando i setter
     */
    public User() {}

    // Questo costruttore mi serve per creare i miei oggetti users
    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        // Imposto la data creazione al momento in cui creo lo user
        this.registrationDate = LocalDateTime.now();
    }


    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    /**
     * Evito il blocco in caso mi restituisce un altro tipo:
     * Collection --> accetto qualsiasi tipo
     * ? extends GrantedAuthority--> accetto qualsiasi tipo di collezione l'importante che ereditano il GrantedAuthortiy
     *
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Per evitare il il cash da NullPointer in caso di ruolo null restituisco una lista vuota
        if (this.role == null) {
            return List.of();
        }
        // Qui trasformo  il ruolo in autorizzazione
        /**
         * List.of --> perchè un utente potrebbe avere + ruoli, ovviamente non qui, quindi è uno step di sviluppo futuro
         * new SimpleGrantedAuthority --> qui creo il mio oggetto che vuole la denominazione iniziale ROLE_ e poi il ruolo specifico
         */
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    // Get speciali legati all'autenticazione dell'utente, qui faccio tutti i return di informazioni o dati utili
    // quindi se l'account non è scaduto (es: periodo di proba), bloccato (es: utente che prova loggarsi n volte sbagliando la password),
    // password non scaduta e utente abilitato

    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    public String getUsername() {
        return this.username;
    }

    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Ovveride perchè sto sovrascrivendo un metodo già esistente
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Getter e setter servono a JPA per accedere ai campi privati in questa classe (incapsulamento, proteggo i miei campi da modifiche esterne)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}