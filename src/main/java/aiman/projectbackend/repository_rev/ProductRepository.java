package aiman.projectbackend.repository_rev;

import aiman.projectbackend.entity_rev.Product;
import org.springframework.data.jpa.repository.JpaRepository;
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
public interface ProductRepository extends JpaRepository<Product, Long> {

    // QUI HO LE MIE IMPLEMENTAZIONI DI QUERY DERIVATE (query semplici):

    /**
     * Tipo di query? Derivate
     * Utilità? Filtro i miei prodotti per nome (senza il case sensitive)
     * Interpretazione di Spring?  SELECT * FROM products WHERE UPPER(name) LIKE UPPER('%name%')
     * Osservazione:
     * UPPER --> trasforma tutti i caratteri in maiuscolo
     * ('%name%') --> % mi serve per cercare il nome in qualsiasi posizione, cioè % è una wildcard che indica qualsiasi carattere
     */
    /** Spiegazione generale
     * List<Product> --> indico il tipo di ritorno, quindi restituisco una lista di prodotti
     * findBy --> indica Select * from in sql
     * Name --> indico il nome del attributo presente nell'entità Product (in cui spring lo cercherà)
     * Containing --> indica LIKE su sql con anche il riferimento alle wildcards "%"
     * IgnoreCase --> indico UPPER() in sql, quindi sia nel db che il nome che ho inserito sarà tutto maiuscolo
     * (String name) --> indico il valore che sto cercando che verrà tradotto nella query sql come WHERE
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Tipo di query? Derivate
     * Utilità? Filtro per categoria tramite ID
     * Interpretazione di Spring? SELECT * FROM products WHERE category_id = XXX
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Tipo di query? Derivate
     * Utilità? Filtro per fasce di prezzo
     * Interpretazione di Spring? SELECT * FROM products WHERE price BETWEEN min AND max
     */
    List<Product> findByPriceBetween(Double min, Double max);

    /**
     * Tipo di query? Derivate
     * Utilità? Filtro per prodotti disponibili con stock maggiore di 0
     * Interpretazione di Spring? SELECT * FROM products WHERE stock_quantity > quantity
     */
    List<Product> findByStockQuantityGreaterThan(Integer quantity);
}

/**
 * Questa parte di repository mi serve per gestire l'ereditarietàà delle classi infatti ho Product come classe principale e
 * component e tool come sottoclassi.
 * Al livello di funzionamento se voglio estrare tutti i prodotti allora in ProductRepository mi estrae sia i prodotti
 * di tipo component e tool
 * Se voglio estrare solo i prodotti di un tipo solo ad esempio component spring mi aggiunge una codnizione alla query
 */

// Rep solo per prodotti di tipo component
@Repository
interface ComponentRepository extends JpaRepository<aiman.projectbackend.entity_rev.Component, Long> {
}

// Rep solo per prodotti di tipo tool
@Repository
interface ToolRepository extends JpaRepository<aiman.projectbackend.entity_rev.Tool, Long> {
}