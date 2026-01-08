package aiman.projectbackend.security_rev.security_config_rev;

import aiman.projectbackend.security_rev.JWTAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// @Configuration --> serve per dire a spring di leggerlo per configurare i componenti dell'applicazione
@Configuration
// @EnableWebSecurity --> serve per abilitare la configurazione di sicurezza standard di spring e anche per attivare le impostazioni personalizzate
@EnableWebSecurity
// @EnableMethodSecurity --> serve ad abilitare le annotazioni sui controller come il @PreAuthorize("hasAuthority('XXX')")
@EnableMethodSecurity
public class SecurityConfig {

    // Inietto i filtri personalizzati che ho fatto in JWTAuthFilter
    @Autowired
    private JWTAuthFilter jwtAuthFilter;

    /**
     * Bean è un oggetto gestito dal contenitore di Spring, quando un oggetto diventa bean lo posso usarlo ovunque nel
     * progetto. Quindi non creo manualmente l'ogetto , ma lo inietto quando mi serve
     * @Bean questa annotazione registra l'oggetto nel contesto di spring
     */
    @Bean
    // Qui definisco il mio filter chain, ovvero controlli ad ogni richiesta http prima di arrivare ai miei controller
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // Disabilito il form di login html predefinito che non mi serve visto che sto lavorando come client con postman
        httpSecurity.formLogin(http -> http.disable());
        // Disabilito la protezione CSRF (cross-site request forgery) che serve per gestire sessioni e cokie
        // nel nostro caso usiamo JWT, ovvero i token che sono allegati ad ogni richiesta quindi non ci serve
        httpSecurity.csrf(http -> http.disable());
        // Si imposta la sessione stateless, quindi di non usare i cookie per ricordare la sessione, ma ogni richiesta
        // http dovrà contenere le credenziali
        httpSecurity.sessionManagement(http -> http.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // Qui specifico di aggiungere il mio filtro personalizzato, ovvero fai controllo del token prima (non dopo la ricerca di username e password)
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Qui imposto regole di autorizzazioni
        httpSecurity.authorizeHttpRequests(http -> http
                //chiunque può accedere ai url per la registrazione e login
                .requestMatchers("/auth/**").permitAll()
                //mi permette di vedere gli errori di accesso nel caso ci fosse
                .requestMatchers("/error").permitAll()
                // Tutte le altre richieste bisogna fare prima il login
                .anyRequest().authenticated()
        );
        // Attivo il CORS (cross-origin resource sharing), quindi cerco il bean corsConfigurationSource (sotto a questo codice)
        // evito che ci sia il blocco delle chiamate, quindi vado a coprire la maggior parde di domini possibili
        httpSecurity.cors(Customizer.withDefaults());

        // Infine costruisco la mia catena e lo restituisco a sprin
        return httpSecurity.build();
    }

    /**
     * Bean è un oggetto gestito dal contenitore di Spring, quando un oggetto diventa bean lo posso usarlo ovunque nel
     * progetto. Quindi non creo manualmente l'ogetto , ma lo inietto quando mi serve
     * @Bean questa annotazione registra l'oggetto nel contesto di spring
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Creo il mio oggetto di configurazione su cui metto le impostazioni qui sotto
        CorsConfiguration configuration = new CorsConfiguration();
        // Indico gli indirizzi su cui dare fiducia e non bloccare nulla
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        // Abilito tutti i metodi http (get,post,put,delete,pathc)
        configuration.setAllowedMethods(List.of("*"));
        // Accetto qualsiasi tipo di header, anche quelli personalizzati
        configuration.setAllowedHeaders(List.of("*"));
        // Creo il mio oggetto di configurazione da applicare a tutti url su cui metto le impostazioni qui sotto
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Qui dico di applicare a tutti gli endpoint le configurazioni viste
        source.registerCorsConfiguration("/**", configuration);
        // Ritorno la sorgente
        return source;
    }
}