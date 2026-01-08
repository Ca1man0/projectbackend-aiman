package aiman.projectbackend.config_rev;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Questa annotazione @Configuration serve per dire a spring di leggerlo per configurare i componenti dell'applicazione
@Configuration
public class CloudinaryConfig {

    //  Con @Value sto andando a leggere i valori in application.properties che a sua volta legge i valori su env.properties
    //  che non sarà pushato su git perchè contengono informazioni sensibili come api key
    @Value("${cloudinary.name}")
    private String cloudName;

    @Value("${cloudinary.apikey}")
    private String apiKey;

    @Value("${cloudinary.secret}")
    private String apiSecret;

    /**
     * Bean è un oggetto gestito dal contenitore di Spring, quando un oggetto diventa bean lo posso usarlo ovunque nel
     * progetto. Quindi non creo manualmente l'ogetto , ma lo inietto quando mi serve
     * @Bean questa annotazione registra l'oggetto nel contesto di spring
     */
    @Bean
    public Cloudinary cloudinary() {
        // Creo l'oggetto cloudinary passando una mappa delle mie credenziali (richiesto da cloduinary, lista chiave valore)
        // ObjectUtils.asMap è un servizio cloudinary per velocizzare la creazione dell'oggetto
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }
}