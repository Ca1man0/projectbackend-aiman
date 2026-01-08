package aiman.projectbackend.repository_rev;

import aiman.projectbackend.entity_rev.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Passo 2 parametri generics, l'entità Category e il tipo di primary key (per dire a Spring come gestire i metodi)
}