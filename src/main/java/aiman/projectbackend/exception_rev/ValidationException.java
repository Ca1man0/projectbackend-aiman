package aiman.projectbackend.exception_rev;

import java.util.List;

// Mi serve per gestire il ritorno dell'eccezioni nel ValidationException

// Qui gestisco l'eccezione di errori di validazione
public class ValidationException extends RuntimeException {

    // Definisco la mia lista in cui metto errori di valdiazione
    private List<String> errorsList;

    // Costruttore che riceve lista di errori
    public ValidationException(List<String> errorsList) {
        // Chiamo RuntimeException con messaggio di errore
        super("Ci sono X errori di validazione");
        //  Salvo la mia lista di errori
        this.errorsList = errorsList;
    }

    // Uso il getter publico così il GlobalExceptionhandler mi legge la lista lo inserisce nel json
    public List<String> getErrorsList() {
        return errorsList;
    }
}