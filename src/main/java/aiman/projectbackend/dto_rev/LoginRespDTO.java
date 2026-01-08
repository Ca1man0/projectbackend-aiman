package aiman.projectbackend.dto_rev;

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

// Mi serve per gestire la restituzione del token (per la sessione) quando l'utente è verificato
public record LoginRespDTO(String accessToken) {
}