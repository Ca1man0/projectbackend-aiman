package aiman.projectbackend.service_rev;

import aiman.projectbackend.dto_rev.OrderRequestDTO;
import aiman.projectbackend.entity_rev.*;
import aiman.projectbackend.repository_rev.OrderRepository;
import aiman.projectbackend.repository_rev.ProductRepository;
import aiman.projectbackend.repository_rev.UserRepository;
import aiman.projectbackend.service_rev.api_rev.ShippingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public class OrderService {

    // Indico le dipendenze che saranno cambiate solo all'interno di questa classe "private" e dopo che sono stati
    // inizializzate non cambiano più "final"
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShippingService shippingService;

    // Costruttore che inietta le repository, quindi spring crea le istanze delle repository e le inietta nel mio servizio
    // qui ho 3 repository e 1 servizio esterno iniettato (calcolo della distanza su cui ho fatto il calcolo della spedizione con tariffa fissa)
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shippingService = shippingService;
    }

    // Metodo 1 - logica di business --> creazione dell'ordine
    public Order createOrderFromDto(OrderRequestDTO dto) {

        //Verifico la presenza dell'utente se non ci fosse lancio l'eccezione
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("creazione ordine fallita perchè l'utente con id: " + dto.getUserId() + " non è stato trovato"));

        // Inizializzo l'ordine, settando i valore come utente, data di creazione ordine e status
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        // Inizializzo la mia variabile total che farà la somma di tuti i prezzi dei prodotti nell'ordine
        double total = 0;
        // Inizializzo una lista vuota di nome items che andrà contenere i miei ordini di tipo OrderItem
        /**
         * OSSERVAZIONE (Recapone tattico):
         * ArrayList<> è vuoto l'operator diamond perchè ho già dichiarato il tipo della lista, ovvero OrderItem
         */
        List<OrderItem> items = new ArrayList<>();

        /** Recapone:
         * OrderRequestDTO.OrderItemRequestDTO --> partendo dal modello di dati che descrivere le singole righe d'ordine salvato nell'ordine generale
         * itemDto : dto.getItems() --> per ogni itemDTO (variabile temporanea) che trovi nella lista dto.getItems mi esegui il codice a graffe
         */
        for (OrderRequestDTO.OrderItemRequestDTO itemDto : dto.getItems()) {

            // Verifichiamo se il prodotto esista nel db guardando il suo id, se non esiste lancio l'eccezione
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("creazione ordine fallita perchè il prodotto con id: " + itemDto.getProductId() + " non è stato trovato"));

            // Creo la singola riga d'ordine in cui ci metto il prodotto, qty, prezzo
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            // Collego l'item all'ordine (stessa logica con address), quindi quando salvo l'articolo scrivi id nella colonna giusta
            item.setOrder(order);

            // Salvo localmente item nella lista items (tracking con il for)
            items.add(item);

            // Tengo a memoria il totale (prezzo per qty)
            total += product.getPrice() * itemDto.getQuantity();
        }

        // Tengo a memoria nell'ordine la lista di items e il totale (dopo che il ciclo for ha finito)
        order.setItems(items);
        order.setTotalAmount(total);

        /** Breve spiegazione dello shipping service:
         * Qui integro il mio servizio di calcolo dello shipping, semplicemente trovo la distanza da una coordinata
         * specifica in cui immaginiamo che sia il mio magazzino del e-commerce e poi ci calcolo la distanza con
         * l'indirizzo con questo servizio API, infine ci ho applicato una tariffa fissa
         */

        // Prima verifico che esista l'indirizzo
        if (user.getAddress() != null) {
            double shipCost = shippingService.calculateShippingCost(
                    user.getAddress().getStreet(),
                    user.getAddress().getCity(),
                    user.getAddress().getZipCode()
            );
            // Salvo il costo dello shipping
            order.setShippingCost(shipCost);
            // Salvo l'ammontare totale (prodotto/i + spedizione)
            order.setTotalAmount(total + shipCost);
        }
        // Salvo il mio ordine con tutti i dati (lista prodotti, costo spedizione, costo totale ...)
        return orderRepository.save(order);
    }

    // Metodo 2 - logica di business --> ottengo tutti gli ordini
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // Metodo 3 - logica di business --> ottengo tutti gli ordini di uno specifico user
    public List<Order> getOrdersByUser(Long userId) {
        
        // Controllo l'esistenza dello user guardando id
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("utente " + userId + " non trovato");
        }
        return orderRepository.findByUserId(userId);
    }

    // Metodo 4 - logica di business --> ottengo la spesa totale dell'user
    public Double getUserTotalSpending(Long userId) {
        Double total = orderRepository.getTotalSpentByUser(userId);
        // Se il totale è diverso da null ritorna totale altrimenti 0
        return (total != null) ? total : 0.0;
    }

    // Metodo 5 - logica di business --> filtri gli ordini in base allo status
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
}