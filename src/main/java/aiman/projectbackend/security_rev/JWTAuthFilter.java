package aiman.projectbackend.security_rev;

import aiman.projectbackend.entity_rev.User;
import aiman.projectbackend.exception_rev.UnauthorizedException;
import aiman.projectbackend.service_rev.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// @Component --> annotazione che serve a Sprin per dire di gestire come bean questa classe e quindi chiunque ha bisogno
// di usare @Autowirde per iniettaserlo
@Component
// Estende OncePerRequestFilter per assicurarci che viene fatto una volta per richiesta http per ridurre duplici chiamate
public class JWTAuthFilter extends OncePerRequestFilter {

    //Inietto jwttools --> mmi serve per generare e verificare il token
    @Autowired
    private JWTTools jwtTools;

    // Inietto  userservice per recuperare i dati dell'utente
    @Autowired
    private UserService userService;

    // Questo fa un filtro alle richieste http prima del controller
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Questo metodo viene richiamato ad ogni richiesta

        try {
            // Cerco nel Header delal richiesta l'Authorization
            String authorizationHeader = request.getHeader("Authorization");

            // Controllo se l'header non è null e se l'autorization inizia con Bearer
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
                throw new UnauthorizedException("Token assente o formastto errato");

            // Se header c'è ed è nel formatto giusto estraggo il token togliendo bearer
            String accessToken = authorizationHeader.substring(7);

            // Verifico la validità del token
            jwtTools.verifyToken(accessToken);

            // Estraggo id nel token
            Long userId = jwtTools.getIdFromToken(accessToken);

            // Cerco con questo id l'utente nel db
            User found = userService.findById(userId);

            // Creo l'oggetto autenticazione e lo passo a spring security
            // I parametri sono X, Y e Z
            // X = l'utente trovato | Y = null perchè non password in chiaro poi l'ho verificato l'utente | Z = prendo i ruoli dell'utente
            Authentication authentication = new UsernamePasswordAuthenticationToken(found, null, found.getAuthorities());

            // Dico a spring che per la durate della richieste l'untente autenticato è questo
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tutto okay, la richiesta lo passo al controller
            filterChain.doFilter(request, response);

        } catch (UnauthorizedException ex) {
            // UnauthorizedException --> ad esempio token mancante o header assente
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
        } catch (Exception ex) {
            //Gestisce errori generici token scaduto, firma non valida ...
            ex.printStackTrace(); // stampiamo l'errore
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "errore filtro " + ex.getMessage());
        }

    }

    // Evito di applicare i filtri in alcuni casi
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Non filtro quando ho l'autenticazione oppure se ho un errore nel sistema non voglio
        // che mi blocchi il msg di errore da visualizzare
        return new AntPathMatcher().match("/auth/**", request.getServletPath()) ||
                new AntPathMatcher().match("/error/**", request.getServletPath());
    }


}