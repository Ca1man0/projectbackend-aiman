package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.ProductDTO;
import aiman.projectbackend.entity_rev.Component;
import aiman.projectbackend.entity_rev.Product;
import aiman.projectbackend.entity_rev.Tool;
import aiman.projectbackend.repository_rev.CategoryRepository; // Importa questo
import aiman.projectbackend.repository_rev.ProductRepository;
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
public class ProductService {

    // Indico le dipendenze che saranno cambiate solo all'interno di questa classe "private" e dopo che sono stati
    // inizializzate non cambiano più "final"
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // Costruttore che inietta le repository, quindi spring crea le istanze delle repository e le inietta nel mio servizio
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Metodo 1 - logica di business --> recupero tutti i prodotto e li converto in DTO
    public List<ProductDTO> getAllProducts() {
        /** Recapone:
         * fidAll() --> recupero tutti i prodotti nel db
         * stream() --> converto questa lista in uno stream per elaborarli
         * map(this::convertToDTO) --> prende il prodotto e lo passi come argomento a convertToDTO, map(product -> this.convertToDTO(product))
         * collect(Collectors.toList()) --> prendo l'output e lo metto in una lista
         */
        return productRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Metodo 2 - logica di business --> salvo il prodotto gestendo anche l'associazione alla categoria completa
    public ProductDTO saveProduct(Product product) {
        // Se il prodotto ha una categoria con un ID, carichiamo la categoria completa dal DB

        // Controllo se il prodotto inviato ha almeno la categoria + Controllo se la categoria esistente abbia id (controllo per evitare il nullpointerexception)
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            // Salviamo id della categoria in una var locale
            Long catId = product.getCategory().getId();
            // Controllo se la categoria esiste nel DB usando id, altrimenti mando su l'eccezzione
            product.setCategory(categoryRepository.findById(catId)
                    .orElseThrow(() -> new RuntimeException("non è stato possibile salvare il prodotto perchè" +
                            "la cateogria con id " + catId + " non è stata trovata")));
        }
        // Salviamo il prodotto nel db tramite repository
        Product savedProduct = productRepository.save(product);
        // Ritorno il dto del prodotto salvato
        return convertToDTO(savedProduct);
    }

    // Metodo 3 - logica di business --> eliminazione prodotto
    public void deleteProduct(Long id) {
        // Verifico prima l'esistenza del prodotto da eliminare guardando id
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("non è stato possibile eliminare il prodotto con id " + id + ", infatti non è stato trovato");
        }
        // Eseguo eliminazione per id
        productRepository.deleteById(id);
    }

    // Metodo 4 - logica di business --> metodo interno (private) per la conversione da entity a dto
    private ProductDTO convertToDTO(Product product) {
        // Dichiaro i campi di default
        String type = "unknown";
        String material = null;
        Double diameter = null;
        String brand = null;
        Boolean isElectric = null;

        // Questo controllo mi serve per gestire il tipo e riempimento di campi specifici in base al tipo
        if (product instanceof Component) { //product is component? --> boolean
            type = "component";
            // Prima faccio un cast della variabile generica spiegando che è un component poi estraggo il material
            material = ((Component) product).getMaterial();
            // Prima faccio un cast della variabile generica spiegando che è un component poi estraggo il diametro
            diameter = ((Component) product).getDiameter();
        } else if (product instanceof Tool) {
            type = "tool";
            // Prima faccio un cast della variabile generica spiegando che è un tool poi estraggo il nome del brand
            brand = ((Tool) product).getBrand();
            // Prima faccio un cast della variabile generica spiegando che è un tool poi estraggo il flag se è elettrico o no
            isElectric = ((Tool) product).getIsElectric();
        }

        // Qui creo il mio dto ritornandolo con tutti i suoi campi poi nel productDTO ho gestito la visualizzazione e non
        // dei campi specifici in base al tipo prodotto con @JsonInclude(JsonInclude.Include.NON_NULL)
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                // Qui gestisco con op ternario la presenza del valore categoria
                product.getCategory() != null ? product.getCategory().getName() : null,
                type,
                material,
                diameter,
                brand,
                isElectric
        );
    }

    /** Osservazione importante:
     *  Alcuni metodi sono identici a quelli presenti in ProductRepository, ad esempio
     *  filterByPrice = findByPriceBetween oppure getAvailableProducts = findByStockQuantityGreaterThan
     *  La differenza che le query presenti nella repo ProductRepository parlando direttamente con il DB restituiscono
     *  entity, invece qui nel service mi permette di gestire output in un formato che voglio con la DTO, restituendo dati
     *  specifici per motivi di sicurezza o pulizia. Quindi se nel futuro volessi cambiare per esempio la logica del filtro
     *  o isolare un campo che non dovrei far vedere con il service lo riesco a fare, invece avere le query
     *  derivate nella repo no
     */

    // Metodo 5 - logica di business --> metodo per cercare il prodotto per nome
    public List<ProductDTO> searchProductsByName(String name) {
        /** Recapone:
         * findByNameContainingIgnoreCase --> si traduce come SELECT * FROM products WHERE UPPER(name) LIKE UPPER('%name%')
         * stream() --> serve per trasformarli in un flusso dati così da elaborarli
         * map(this::convertToDTO) --> si traduce in map(product -> this.convertToDTO(product)) dove prende il prodotto e lo passi come argomento a convertToDTO
         * collect(Collectors.toList()) --> prendo l'output e lo metto in una lista
         */
        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Metodo 6 - logica di business --> metodo per filtrare il prezzo
    public List<ProductDTO> filterByPrice(Double min, Double max) {
        /** Recapone:
         * findByPriceBetween(min, max) --> si traduce come SELECT * FROM products WHERE price >= min AND price <= max
         * stream() --> serve per trasformarli in un flusso dati così da elaborarli
         * map(this::convertToDTO) --> si traduce in map(product -> this.convertToDTO(product)) dove prende il prodotto e lo passi come argomento a convertToDTO
         * collect(Collectors.toList()) --> prendo l'output e lo metto in una lista
         */
        return productRepository
                .findByPriceBetween(min, max)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getAvailableProducts() {
        /** Recapone:
         * findByPriceBetween(min, max) --> si traduce come SELECT * FROM products WHERE stock_quantity > 0
         * stream() --> serve per trasformarli in un flusso dati così da elaborarli
         * map(this::convertToDTO) --> si traduce in map(product -> this.convertToDTO(product)) dove prende il prodotto e lo passi come argomento a convertToDTO
         * collect(Collectors.toList()) --> prendo l'output e lo metto in una lista
         */
        return productRepository
                .findByStockQuantityGreaterThan(0)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}