package aiman.projectbackend.service_rev.api_rev;

// leggo i valori nel file properties che a sua volta fa riferimento al file env che non sarà pushato su git
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

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
public class ShippingService {

    // Inietto la chiave di questa api per l'estrazione della distanza dal magazzino all'indirizzo del cliente
    @Value("${ors.api.key}")
    private String apiKey;

    // Qui inietto tutti i parametri che mi servono per calcolare il costo della spedizione che gli estraggo in application.properties
    // ovviamente sono parametri inventati per semplicità, se volessi fare una cosa sofisticata dovevo trovare un api che mi permetteva
    // di indicare anche il tipo di spedizione e scegliere il corriere in base al prezzo
    @Value("${shipping.warehouse.coords}")
    private String warehouseCoords;
    @Value("${shipping.rate.per.km}")
    private double ratePerKm;
    @Value("${shipping.fallback.cost}")
    private double backupcost;


    // Metodo 1 - logica di business --> calcolo costo spedizione
    public double calculateShippingCost(String street, String city, String zip) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Qui devo creare url per inviare la richiesta di coordinate al specifico indirizzo inserito dall'utente
            String geoUrl = "https://api.openrouteservice.org/geocode/search?api_key=" + apiKey +
                    "&text=" + street + " " + city + " " + zip;
            // Qui invio la richiesta GET con url che ho creato prima, restituendomi un albero generico per semplificare la lettura
            JsonNode geoJson = restTemplate.getForObject(geoUrl, JsonNode.class);
            // Albero ricevuto chiedo di andare a prendere le coordinate
            /** Esempio di struttura json
             * {
             *   "features": [
             *     {
             *       "geometry": {
             *         "coordinates": [X.XX, Y.YY]
             *       }
             *     }
             *   ]
             * }
             */
            JsonNode coords = geoJson.path("features").get(0).path("geometry").path("coordinates");
            // Unisco la longitudine e la latitudine per formare la mia coordinata
            String destination = coords.get(0).asDouble() + "," + coords.get(1).asDouble();
            // Qui costruisco url per chiedere il percorso dal mio magazzino all'address dell'utente
            String routeUrl = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + apiKey +
                    "&start=" + warehouseCoords + "&end=" + destination;
            // Qui faccio la richiesta con url chiedendo i dati in formato ad albero
            JsonNode routeJson = restTemplate.getForObject(routeUrl, JsonNode.class);
            // qui ricavo la distanza del percorso trovato e lo divido per 1000 perchè sono in metri e non in km
            double distanceKm = routeJson.path("features").get(0).path("properties").path("summary").path("distance").asDouble() / 1000;
            // Ritorno con il costo totale della spedizione
            return distanceKm * ratePerKm;
        } catch (Exception e) {
            // Se nel caso ho qualche errore di chiamata api per ottenere la risposta mando questo print di errore e ritorno con la tariffa fissa di spedizione
            System.err.println("problema con il calcolo della spedizione" + e.getMessage());
            return backupcost;
        }
    }
}