package aiman.projectbackend.dto_rev;

import java.time.LocalDateTime;

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

public class UserDTO {
    // Indico i campi che deve avere user, ovvero id, username, email, nome, cognome, data registrazione,
    // campo del url profilo (inizialmente vuoto, verrà caricato dall'utente con endpoint patch), address
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDateTime registrationDate;
    private String profileImageUrl;
    private AddressDTO address;

    // Costruttore vuoto per la de/serializzazione del JSON, ovvero quando postman (client) invia i dati JSON questo costruttore
    // mi permette di creare un oggetto Java vuoto su cui andrò a riempire
    public UserDTO() {}

    // Costruttore per la creazione del DTO nel service
    public UserDTO(Long id, String username, String email, String firstName, String lastName,
                   LocalDateTime registrationDate, String profileImageUrl, AddressDTO address) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationDate = registrationDate;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}