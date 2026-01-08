package aiman.projectbackend.service_rev.api_rev;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

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
public class CloudinaryService {

    // Indico la dipedenza che sarò cambiata solo all'interno di questa classe "private" e dopo che lo inizializzo
    // non posso cambiarla più "final"
    private final Cloudinary cloudinary;

    // Costruttore che inietta il bean configurato in CloudinaryConfig, avendo già le key api da usare
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // Metodo 1 - logica di business --> caricamento dell'immagine

    /**
     * MultipartFile --> file binario che l'utente carica tramite postman, contiene oltre l'immagine anche altri dati
     * throws IOException --> per gestire problematiche in caso di caricamento dell'immagine
     */
    public String uploadImage(MultipartFile file) throws IOException {
        /**
         * cloudinary.uploader().upload() --> qui ho la chiamata API, invio a cloudinary i byte e poi si costruisce l'immagine, lo salva e genera url
         * file.getBytes() --> il file immagine è trasformato in un array di byte e poi trasmesso a cloduinary
         * ObjectUtils.emptyMap() --> qui posso passare opzioni come taglio o ridimensionamento dell'immagine, qui passo una mappa vuota quindi le impostazioni sono default
         */
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        // Qui faccio un ritorno del url trasformato in stringa che lo salvo nel user
        return uploadResult.get("url").toString();
    }
}