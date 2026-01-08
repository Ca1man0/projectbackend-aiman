package aiman.projectbackend.security_rev;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.exception_rev.UnauthorizedException;

import java.util.Date;

// @Component --> annotazione che serve a Sprin per dire di gestire come bean questa classe e quindi chiunque ha bisogno
// di usare @Autowirde per iniettaserlo
@Component
public class JWTTools {

    // Riporto la chiave segreta per la generazione dei miei token, è variabile ambiente e non nel applicaiton properties
    // perchè è segreta
    @Value("${jwt.secret}")
    private String secret;

    // Creo i token per coloro che hanno fatto il login
    public String createToken(User user) {
        return Jwts.builder()
                // Indico la data emissione del token
                .issuedAt(new Date(System.currentTimeMillis()))
                // Data di scadenza del token, ad esempio 7 gg
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                // Inseriamo id utente proprietario del token
                .subject(String.valueOf(user.getId()))
                // Qui avviene la firma del token, combina l'agoritmo con il nostro segreto, una modifica dei campi
                // precedenti portano a una variazione signficiative del hash
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                // Compatta tutto in una stringa url
                .compact();
    }

    // Qui verifico la validità del token
    public void verifyToken(String token) {
        try {
            // uso jwts.parser per la lettura del token
            Jwts.parser()
                    // configuro il parse con la chiave segreta usata nella firma, se la forma del token non
                    // corrisponde a quella calcolata col segreto mi dai errore
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    // Costruisce l'stanza del parser configurata
                    .build()
                    // Prova a leggere il token se è scaduto per esempio lancio una specifica eccezione
                    .parse(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Token non valido rifai il login");
        }
    }

    // Estrago id dell'utente dal token, serve per capire chi fa la richiesta
    public Long getIdFromToken(String token) {
        // Configurazione del lettore
        String id = Jwts.parser()
                // Verifica se il token è firmato con il mio segreto
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                // Costruisce l'stanza del parse configurata
                .build()
                // LEgge il token se è scaduto lancia eccezione
                .parseSignedClaims(token)
                // Accesso ai dati, quindi recuperiamo il corpo del json contenuto nel token
                .getPayload()
                // Estraco il subject che sarebbe il proprietario del token
                .getSubject();
        // Lo riconverto visto che JWT tratta i subject come stringa e non numeri
        return Long.parseLong(id);
    }
}