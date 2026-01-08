package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.AddressDTO;
import aiman.projectbackend.entity_rev.Address;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.repository_rev.AddressRepository;
import aiman.projectbackend.repository_rev.UserRepository;
import org.springframework.stereotype.Service;

/** BREVE TEORIA DEL SERVICE
 * DEF: Nel service ho la mia logica di business, quindi qui mostro come deve funzionare la mia applicazione back-end
 *
 * CARATTERISTICHE:
 * 1) Risiede la logica di business, ad esempio il calcolo del prezzo totale dei ordini
 * 2) Ha il ruolo di intermediario tra il Controller (gestisce le richieste HTTP) e Repository (comunica con il DB)
 * 3) Disaccopia, quindi il Controller non deve sapere come sono processati i dati, deve solo delegare il servizio richiesto
 */

// @Service è un annotazione serve per dire a spring che ho a che fare con un service e dice anche di creare una singola
// istanza all'avvio di questa classe
@Service
public class AddressService {

    // Indico le dipendenze che saranno cambiate solo all'interno di questa classe "private" e dopo che sono stati
    // inizializzate non cambiano più "final"
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // Costruttore che inietta le repository, quindi spring crea le istanze delle repository e le inietta nel mio servizio
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    // Metodo - logica di business --> associo l'indirizzo a un utente e lo salvo
    public AddressDTO addAddressToUser(Long userId, Address address) {

        // Verifico l'esistenza dell'utente, se non esiste lancio l'eccezione
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("non riesco ad associare l'utente, infatti utente con id " + userId + " non è stato trovato"));

        /**
         * Collego l'indirizzo dalla tabella Address con quella del User, specificando il salvataggio dell'ID user
         * ES:
         * BEFORE => ho un user Aiman con id = 1 e un oggetto address "via Roma" --> il campo address in user è null
         * AFTER =>  ho un oggetto Address con il riferimento a User --> dal address ricavo lo user
         * RESULT => JPA saprà in quale id del user devo salvare l'indrizzo
         */
        address.setUser(user);

        //
        /**
         * Salvo il mio address nel DB sapendo nel passaggio precedente dove salvare l'indirizzo
         *
         * Quindi nello step precedente dopo il collegamento, andrò a salvare i miei dati indirizzo, avendo a memoria
         * id dell'utente, facendo la INSERT
         */
        Address saved = addressRepository.save(address);

        // Ritorniamo il DTO e non entity sempre con l'obiettivo di non esporre dati sensibili o informazioni tecniche
        return new AddressDTO(saved.getId(), saved.getStreet(), saved.getCity(), saved.getZipCode());
    }
}