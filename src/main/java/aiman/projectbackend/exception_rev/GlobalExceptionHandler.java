package aiman.projectbackend.exception_rev;

import aiman.projectbackend.exception_rev.exception_dto_rev.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import aiman.projectbackend.exception_rev.exception_dto_rev.ErrorWithListDTO;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

// Annotazione @ControllerAdvice mi permette di dire a spring che tutte le eccezione lanciate dai controller vengono gestiti qui
@ControllerAdvice
public class GlobalExceptionHandler {

    // MethodArgumentNotValidException --> INPUT CON FORMATO NON VALIDO --> 400

    // Annotazione ExceptionHandler mi permette di indicare che tipo di eccezione andrò a gestire in questa classe ovvero MethodArgumentNotValidException
    // Qui gestisco i miei errori personalizzati --> VALIDAZIONE
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        /** Recapone:
         * ex.getBindingResult()
         * getFieldErrors() --> recupero tutti gli errori, ad esempio email obbligatoria
         * stream() --> lo trasformo in flusso così da elaborare questi errori
         * map(error -> error.getField() + ": " + error.getDefaultMessage()) --> per ogni errore avrò una stringa con il campo in cui è esplosa l'eccezione e il messaggio di default
         * collect(Collectors.joining(", ")) --> transformo in una lista, magari potrei avere più errori e quindi devo indicarli tipo email non valida, password corta ...
         */
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        // Qui ho un dto in cui sto preparando l'errore che invierò a postman o client
        ErrorResponse errorResponse = new ErrorResponse(

                // In questo pacchetto che invierò al cliente avrò:

                // L'errore 400 ovverò i dati inviati non stanno seguendo uno standard corretto, es: email non ti metto la @
                HttpStatus.BAD_REQUEST.value(),
                //  Questo è il msg di errore breve
                "Validation Failed",
                // Qui farò vedere il messaggio dettagliato del singolo o più errori
                errors,
                // Qui restituisco url formattato
                /** Recapone:
                 * getDescription(false) --> ottengo un url lungo con altri dati, mettendo false estraggo il path della chiamata endpoint in cui ho avuto il problema
                 * replace("uri=", "") --> tolgo uri per far vedere il path del endpoint pulito
                 */
                request.getDescription(false).replace("uri=", "")
        );
        // Restituisco l'errore formattato e lo status di bad request 400
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // RuntimeException --> CRASH CODICE --> 500

    // Annotazione ExceptionHandler mi permette di indicare che tipo di eccezione andrò a gestire in questa classe ovvero RuntimeException.class
    // RuntimeException.class --> qui mi permette di gestire errori di run time nullpointerexception o eccezioni personalizzate come risorse non trovate (ad esempio utente o prodotto non esistente)
    // Qui gestisco i miei errori personalizzati --> RUNTIME
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) { // ho il messaggio dell'errore e url in cui ho avuto il crash
        // Qui ho un dto in cui sto preparando l'errore che invierò a postman o client
        ErrorResponse error = new ErrorResponse(

                // In questo pacchetto che invierò al cliente avrò:

                // L'errore 500 ovverò problemi con il server
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                //  Questo è il msg di errore breve
                "Internal Server Error",
                // Qui oh il messaggio lanciato dall'eccezione, ad esempio i messaggi che ho salvato quando non trovo id
                ex.getMessage(),
                /** Recapone:
                 * getDescription(false) --> ottengo un url lungo con altri dati, mettendo false estraggo il path della chiamata endpoint in cui ho avuto il problema
                 * replace("uri=", "") --> tolgo uri per far vedere il path del endpoint pulito
                 */
                request.getDescription(false).replace("uri=", "")
        );
        // Restituisco l'errore formattato con il suo status 500
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Exception --> QUALSIASI ERRORE NON COPERTO IN GENERALE --> 500

    // Annotazione ExceptionHandler mi permette di indicare che tipo di eccezione andrò a gestire in questa classe ovvero Exception.class
    // Exception.class --> Qui gestisco tutti i possibili errori che non ho coperto nella gestione precedente
    // Ad esempio errori nel db, problemi di memori ecc...
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {

        ErrorResponse error = new ErrorResponse(
                // In questo pacchetto che invierò al cliente avrò:

                // L'errore 500 ovverò problemi con il server
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                //  Questo è il msg di errore breve
                "Internal Server Error",
                // Qu ho il messaggio di errori esplicito e in lingua italiana (comprensibile dai customer italiani)
                "Si è verificato un errore imprevisto.",
                /** Recapone:
                 * getDescription(false) --> ottengo un url lungo con altri dati, mettendo false estraggo il path della chiamata endpoint in cui ho avuto il problema
                 * replace("uri=", "") --> tolgo uri per far vedere il path del endpoint pulito
                 */
                request.getDescription(false).replace("uri=", "")
        );
        // Restituisco l'errore formattato e lo status di 500 (problema del server)
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ValidationException --> Errore di input dati che non seguono la validazione --> 400

    // Qui gestione l'eccezione personalizzata, ovvero della ValidationException, dati non validi quando invio al client
    // per password o login
    @ExceptionHandler(ValidationException.class)
    // Se dovesse avvenire l'erroee ho la restituzione dello status 400
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorWithListDTO handleValidationErrors(ValidationException ex) {
        // Restituisco un DTO che contiene le seguente informazioni all'interno del messaggio d'errore
        return new ErrorWithListDTO(
                //  Messaggio d'ecezione
                ex.getMessage(),
                // Quando ho avuto l'errore
                LocalDateTime.now(),
                // Indico i campi non validi (è una lista visto che posso avere n errori)
                ex.getErrorsList()
        );
    }

    // AccessDeniedException --> Errore di non accesso per autorizzazione per esempio --> 403

    // Qui ho gestito gli errori del 403 forbidden --> ad esempio token non valido o ruolo insufficiente
    // Indico l'eccezione di riferimento per gestila ovvero AccessDeniedException
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception ex, WebRequest request) {
        // Qui costruisco il mio errore indicando le seguenti informazioni
        ErrorResponse error = new ErrorResponse(
                // Codice 403 del forbidden
                HttpStatus.FORBIDDEN.value(),
                // Riferimento ad un msg di errore breve
                "Access Denied",
                // Riferimento ad un msg di errore lungo
                "Non hai i permessi necessari per accedere a questa risorsa.",
                // Recupero solo url per indicare dove ho avuto l'errore (false perchè se no mi restituisce l'errore con altre informazioni che non mi servono)
                request.getDescription(false).replace("uri=", "")
        );
        // Restituisco l'errore e lo status
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
}