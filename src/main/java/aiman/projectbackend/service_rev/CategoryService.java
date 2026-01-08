package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.CategoryDTO;
import aiman.projectbackend.entity_rev.Category;
import aiman.projectbackend.repository_rev.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
public class CategoryService {

    // Indico la dipedenza che sarò cambiata solo all'interno di questa classe "private" e dopo che lo inizializzo
    // non posso cambiarla più "final"
    private final CategoryRepository categoryRepository;

    // Costruttore che inietta le repository, quindi spring crea le istanze delle repository e le inietta nel mio servizio
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Metodo 1 - logica di business --> ottengo tutte le categorie
    public List<CategoryDTO> getAllCategories() {
        /** Spiegazione sintetica (recapone):
         * List<CategoryDTO> --> indica il tipo di ritorno, restituisce un contenitore pieno di oggetti di tipo CategoryDTO
         * categoryRepository --> chiedo alla repo che sto interfacciando
         * findAll --> recupero le righe della tabella categories
         * stream --> trasforma la lista in un flusso dati per elaborarli
         * map --> trasforma le categorie in un CategoryDTO | cat è un nome inventato per riferici ogni categoria, dove
         * per ogni categoria creo un ogetto DTO composto da id e nome della categoria
         * collect --> raggruppo tutto (ovvero i miei oggetto DTO della categoria) in una lista finale
         */
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryDTO(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }

    // Metodo 2 - logica di business --> salvo la categoria e restituisco DTO
    public CategoryDTO saveCategory(Category category) {
        // Salvo la categoria nel db facendo un insert
        Category saved = categoryRepository.save(category);
        // Restituisco il mio dto, indicando id e nome categoria
        return new CategoryDTO(saved.getId(), saved.getName());
    }

    // Metodo 3 - logica di business --> cancello la categoria
    public void deleteCategory(Long id) {

        // Verifico prima che esista la mia categoria da cancellare, se non esiste lancio un eccezzione
        if (!categoryRepository.existsById(id)) { // Qui faccio una query per vedere se esiste la categoria, se è diverso da true allora lancia l'eccezione
            throw new RuntimeException("cancellazione non riuscita perchè categoria con id: " + id + " non è stata trovata");
        }
        // Rimuovo la riga nel db
        categoryRepository.deleteById(id);
    }
}