# Project back-end su e-commerce di Aiman Hamdouni

## Recap del progetto
È stato realizzato un backend per una piattaforma e-commerce dove ho provato a gestire aspetti come autenticazione (con JWT), gestione dei ruoli
catalogo prodotti, creazione ordine, caricamento immagine e calcolo della spedizione.
I servizi che sono stati integrati sono
- **Cloudinary** per l'archiviazione delle immagini 
- **OpenRouteService** per la logistica delle spedizioni basata sulla geolocalizzazione. 

## Funzionalità

- Gestione della parte di registrazione utente con anche la distinzione dei ruoli
- Login utente con la restituzione del token di sessione per utilizzare la piattaforma
- Gestione del profilo, caricamento immagine con Cloudinary e inserimento del proprio indirizzo
- Gestione eliminazione del singolo utente o tutti gli utenti (quest'ultima era solo un test che mi serviva per rifare le prove)
- Gestione del catalogo dei prodotti attraverso l'uso dei filtri (ricerca nome, fascia per prezzo e prodotti disponibili)
- Creazione dell'ordine seguita anche il calcolo del costo di spedizione
- Visualizzazione della lista d'ordine
- Visualizzazione anche della spesa totale del singolo utente

## Tecnologia usata

- Linguaggio java 21
- Framework è spring boot 3.4.0
- Database è postegresql
- API esterne sono Cloudinary (immagini) e OpenRouteservice (per il calcolo della distanza)

## Variabili ambiente e configurazione

1) All'interno del progetto è assente il file env.properties, perciò bisognerà crearlo nella directory del progetto visto che
si collega con il file application properties dove ho i miei paramentri o variabili nascoste

Qui mostro l'esempio del env.properties:

```properties
# Configurazione DB 
PG_USERNAME= postgres
PG_PASSWORD=tua_password_postgres
PG_DB_NAME= nome_db_tuo

# API per il calcolo distanza di spedizione --> Openruteservice
# Richiedi una chiave su: (https://openrouteservice.org/)
OPENROUTE_KEY=tua_chiave_api_openroute

# API per il caricamento di immagine --> Cloudinary
CLOUDINARY_NAME=tuo_cloud_name
CLOUDINARY_KEY=tua_api_key
CLOUDINARY_SECRET=tua_api_secret

# JWT per la sicurezza
JWT_SECRET=indica_la_tua_chiave_segreta_almeno_32_caratteri

# PARAMETRI SPEDIZIONE
COORD_WH = inserisci_coordinate_magazzino
SPED_KM = inserisci_tariffa_al_km
SPED_KM_DEFAULT = inserisci_tariffa_al_km

```

2) Dovrai andare su pgadmin è creare il tuo db di partenza su cui andrai a provare gli endpoint
ricorda che deve avere lo stesso nome di PG_DB_NAME

3) Quando avii il db per la prima volta devi registrarti con endpoint come admin o superadmin per creare la prima categoria 
che sarà associata poi ai prodotti che andrai creare per semplicità l'avevo impostato come test id = 1 su postman

4) Su postman quando fai login per semplicità ricorda di impostare il token all'interno della variabile globale della collezione
dei endpoint. Per farlo segui questi passaggi:
- In alto a destra è indicato "No environment" cliccaci e poi premi al "+"
- Vedrai una schermata in cui su variable inserisci "token" e invece su value andrai a inserire il token
- Ciascun endpoint verifica nella sezione Authorization che sia selezionato come Auth Type "Inherit auth from parent", dopodiché
a destra dovrai visualizzare la tua variabile globale "{{token}}"

5) Ricordati ti scaricare le api key e inserirle nel env.properties, qui ti metto i link di riferimento dove ti registri:
- Sito per API di Cloudinary: https://cloudinary.com/
- Sito per API di OpenRouteService: https://openrouteservice.org/

## Osservazioni

1) API di OpenRouteService calcola solo la distanza poi dentro io ci applico la tariffa usando i paramentri che gli specifico
se come esempio puoi utilizzare questi dove indico coordinate del mio magazzino, tariffa, tariffa default.
   COORD_WH = 9.1900,45.4642
   SPED_KM = 0.55
   SPED_KM_DEFAULT = 15.0

2) Su postman in alcuni casi tipo la generazione prodotti di tipo component ho usato le variabili dinamiche {{$random ... }}
per generare n prodotti e testare il suo funzionamento, essendo random andrà a generare prodotti non inerenti al mondo idraulico
per questo motivo visualizzare dei dati strani

3) Stai attento alla gestione dei ruoli, infatti alcuni endpoint non sono accessibili al singolo user o admin. Qui ti elenco un
riassunto delle autorizzazioni per ciascun endpoint:

- AuthController
  - /auth/login - POST --> TUTTI
  - /auth/register - POST --> TUTTI
  - /auth/register/admin - POST --> TUTTI (in produzione questo endpoint sarebbe protetto)
  - /auth/register/superadmin - POST --> TUTTI (in produzione questo endpoint sarebbe protetto)
- AdressController
  - /api/addresses/user/{userId} - POST --> utente con lo stesso ID o superadmin
- CategoryController
  - /api/categories - GET --> TUTTI
  - /api/categories - POST --> solo admin o superadmin
  - /api/categories/{id} - DELETE --> solo admin o superadmin
- OrderController
  - /api/orders - POST --> utente con lo stesso ID o superadmin
  - /api/orders - GET --> solo admin o superadmin
  - /api/orders/user/{userId}/total - GET --> utente proprietario, admin o superadmin
  - /api/orders/user/{userId} - GET --> utente proprietario, admin o superadmin
  - /api/orders/status - GET --> admin o superadmin
- ProductController
  - /api/products - GET --> TUTTI
  - /api/products - POST --> admin o superadmin
  - /api/products/{id} - GET --> admin o superadmin
  - /api/products/search - GET --> TUTTI
  - /api/products/filter - GET --> TUTTI
  - /api/products/available - GET --> TUTTI
- UserController
  - /api/users - GET --> admin o superadmin
  - /api/users - POST --> admin o superadmin
  - /api/users/{id} - GET --> utente proprietario, admin o superadmin
  - /api/users/{id} - DELETE --> admin o superadmin
  - /api/users/{id}/profile-image - PATCH --> proprietario o superadmin

 4) Stai attento quando usi postman, ovvero in base ai permessi sopra devi gestire il tuo id di riferimento, ovvero devi ricordati il proprio id o quello in cui hai intenzione di fare un'attività (ad esempio upload di un'immagine), quindi è meglio tenere la tabella su pgadmin degli user aperta

 5) Ricorda che per ogni sessione devi caricare l'immagine profilo su postman nella sezione body, metti in key "file" e su "value" fa upload


