package aiman.projectbackend.repository_rev;

import aiman.projectbackend.entity_rev.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/** BREVE TEORIA DELLA REPOSITORY
 * DEF: la repository fa da interfaccia tra le mie classi java e  il mio database
 *
 * CARATTERISTICHE:
 * 1) Astraggo il mio database, quindi non sto a scrivere sql manuali, ma estendo Jparepository in cui posso usare dei
 * metodi già presenti come save, findbyid ...
 * 2) Posso usare le query derivate, infatti spring JPA è in grado di generare la query sql indicando il metodo, ad
 * esempio findBy me lo converte in una query
 *
 * OSSERVAZIONE:
 * Estendere Jparepository, spring andrà a generare l'implementazione di questa interfaccia
 */

// Specifico l'annotazione Repository per completezza, ma non è necessario
// perchè spring lo capisce quando lo estendi JpaRepository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // QUI HO La MIA IMPLEMENTAZIONI DI QUERY DERIVATA (query semplice):
    /**
     * Tipo di query? Derivate
     * Utilità? Cerco l'utente tramite email (utile per login)
     * Interpretazione di Spring?  SELECT * FROM users WHERE email = XXX
     */
    /** Spiegazione generale
     * Optional<User> --> indico il tipo di ritorno, quindi restituisco l'utente se esiste altrimenti restituisco campo vuoto (vedi Optional)
     * findBy --> indica Select * from in sql
     * Email --> indico la email del attributo presente nell'entità Email (in cui spring lo cercherà)
     * (String email) --> indico il valore che sto cercando che verrà tradotto nella query sql come WHERE
     */
    Optional<User> findByEmail(String email);
}