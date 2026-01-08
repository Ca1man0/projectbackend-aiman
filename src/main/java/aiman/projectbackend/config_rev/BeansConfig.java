package aiman.projectbackend.config_rev;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Teoria:
 * Questa classe mi serve per centralizzare la parte di gestione delle crittografia delle password. Serve a Spring security
 */

// Questa annotazione @Configuration serve per dire a spring di leggerlo per configurare i componenti dell'applicazione
@Configuration
public class BeansConfig {

    /**
     * Bean è un oggetto gestito dal contenitore di Spring, quando un oggetto diventa bean lo posso usarlo ovunque nel
     * progetto. Quindi non creo manualmente l'ogetto , ma lo inietto quando mi serve
     * @Bean questa annotazione registra l'oggetto nel contesto di spring
     */
    @Bean

    public PasswordEncoder getBCrypt() {
        /**
         * Qui mi cripta la mia password, dove 10 (2^10 interazioni) è un parametro che indica la complessità per il calcolo del hash
         * più altro questo numero e più sarà difficile da individuare la password con le combinazioni
         * Quindi occorre un trade off tra tempo per la generazione di questo hash e il livello di sicurezza
         */
        return new BCryptPasswordEncoder(10);
    }
}