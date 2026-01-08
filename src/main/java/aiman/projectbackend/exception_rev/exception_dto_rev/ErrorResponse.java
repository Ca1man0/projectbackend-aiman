package aiman.projectbackend.exception_rev.exception_dto_rev;

import java.time.LocalDateTime;

/** BREVE TEORIA DEI DTO
 *
 * DEF: DTO (Data transfer object) è un oggetto che trasferisce i dati tra diversi layer dell'applicazione, ad esempio Controller --> Service
 *
 * CARATTERISTICHE:
 * 1) DTO non sono mappati direttamente sulle tabelle del database e non sono entità
 * 2) Con i DTO decido quali dati esporre, infatti risulta rischioso esporre le entità del database tramite API REST perchè
 * potrei avere campi sensibili o dettagli tecnici da non far vedere
 * 3) DTO evita che API sia legato strettamente allo schema del database, quindi se cambio le colonne della tabella API continuerà a funzionare
 * 4) DTO mi permettono di fare validazione come il @Notnull o @Size prima che arrivino al service
 */

// Questo è un dto dove definisco la struttura json dell'errore che invierò al client, ovvero postman

// Questo è il mio DTO per gestire errori di notfound, unauthorized ...
public class ErrorResponse {

    // Dichiaro i campi del mio dto

    // Orario in cui è accaduto l'errore
    private LocalDateTime timestamp;
    // Il tipo di errore, ad esempio 400 bad request, 404 not found o 500 error server
    private int status;
    // Messaggio di errore breve (spiegano lo status)
    private String error;
    // Messaggio di errore più esplicativo
    private String message;
    // Url in cui è accaduto l'errore
    private String path;

    // Costruttore per la creazione del DTO
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Getter e Setter per l'accesso ai campi privati. Anche se vedo che alcuni non sono usati, quando il controller riceverà
    // un JSON dal client userà questi metodi per iniettare i valori nel campo o leggere valori dell'oggetto e trasformarli in JSON da
    // far vedere al client (postman)

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}