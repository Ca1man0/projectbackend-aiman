package aiman.projectbackend.repository_rev;

import aiman.projectbackend.entity_rev.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Passo 2 parametri generics, l'entità Order e il tipo di primary key (per dire a Spring come gestire i metodi)

    // QUI HO LE MIE IMPLEMENTAZIONI DI QUERY DERIVATE (query semplici):

    /**
     * Tipo di query? Derivate
     * Utilità? Cerco gli ordini di uno specifico utente
     * Interpretazione di Spring?  SELECT * FROM orders WHERE userId = XXX
     */
    /** Spiegazione generale
     * List<Order> --> indico il tipo di ritorno, quindi restituisco una lista di ordini
     * findBy --> indica Select in sql
     * UserId --> indico il nome del attributo presente nell'entità Order (in cui spring lo cercherà)
     * (Long userId) --> indico il valore che sto cercando che verrà tradotto nella query sql come WHERE
     */
    List<Order> findByUserId(Long userId);

    /**
     * Tipo di query? Derivate
     * Utilità? Cerco gli ordini con uno stato specifico
     * Interpretazione di Spring?  SELECT * FROM orders WHERE status = YYY
     */
    List<Order> findByStatus(String status);

    // QUI HO LA MIA IMPLEMENTAZIONI DI QUERY JPQL:

    /** RECAPONE TEORICO:
     * JPQL è un linguaggio di query orientato agli oggetti, quindi io non interrogo le tabelle del database che
     * visualizzo di pgadmin, ma le classi entità
     */

    /**
     * Tipo di query? JPQL
     * Utilità? Calcolo il totale della spesa del singolo utente
     */
    /** Spiegazione generale
     * Prima parte:
     * @Query --> serve per indicare a Spring di usare la query all'interno della stringa
     * o --> è alias di Order
     * SUM --> è la funzione di aggregazione
     * o.user.id --> vai all'ordine del user e prendi id
     * :userId --> ":" indica che questo campo verrà iniettato
     *
     * Seconda parte:
     * Double --> è il tipo di ritorno
     * @Param("userId") --> serve per indicare il parametro da iniettare
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.user.id = :userId")
    Double getTotalSpentByUser(@Param("userId") Long userId);
}