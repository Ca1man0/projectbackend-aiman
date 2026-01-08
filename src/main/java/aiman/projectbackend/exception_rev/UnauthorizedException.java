package aiman.projectbackend.exception_rev;

// Mi serve per gestire il ritorno dell'eccezioni nel GlbalExceptioHandler

// Qui gestisco l'eccezione di autorizzazione
public class UnauthorizedException extends RuntimeException {

    // Costruttore che prende il messaggio di token o ruolo non valido
    public UnauthorizedException(String message) {
        //  Passa il msg al padre RuntimeException e restituisce il log
        super(message);
    }
}