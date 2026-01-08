package aiman.projectbackend.entity_rev;

/**
 * Qui definisco un enum sul ruolo, indicando un numero limitato di ruoli / elementi, questo è un modo per avere maggior
 * sicurezza nei endpoint qualora il ruolo fosse diverso o non ha l'autorizzazione per quel specifico endpoint
 */
public enum Role {
    USER,
    ADMIN,
    SUPERADMIN

}
