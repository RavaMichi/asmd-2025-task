# Lab 01 - Require

> Advanced Software Modeling and Design - 2024/25
>
> Author: *Ravaioli Michele*
> E-mail: *michele.ravaioli3@studio.unibo.it*

## Abstract

Lo scopo di questo task e' quello di analizzare requisiti e specifiche di un piccolo progetto, e successivamente di catturarle completamente utilizzando il linguaggio **Gherkin**. Infine, e' richiesto di validare i pregi e i difetti dell'utilizzo di Gherkin per la formulazione dei requisiti. 

Il progetto scelto da analizzare per il task e' un servizio di noleggio di biciclette elettriche. I requisiti del sistema non solo comprendono requisiti funzionali (come richiesta noleggio di una bici, fine noleggio di una bici, ...) ma anche requisiti non-funzionali (reattivita', disponibilita' del sistema).

## Analisi

### Requisiti

Il progetto scelto riguarda un *servizio di noleggio di e-bike*.
Il servizio deve permettere agli utenti di registrarsi creando un nuovo account, scegliendo un nome utente univoco e una password. Una volta creato l’account, l’utente deve poter accedere con le proprie credenziali per utilizzare le funzionalita' del sistema. L’account viene inizializzato con un saldo crediti pari a zero, che può essere successivamente ricaricato dall’utente attraverso un’operazione di pagamento. La ricarica deve consentire di incrementare il credito disponibile.

Il sistema deve permettere all’utente di connettersi a una bicicletta elettrica, identificata da un ID univoco, a condizione che disponga di un credito sufficiente e che la bicicletta non sia gia' in uso da altri. Durante la connessione, il sistema registra l’associazione tra utente ed e-bike e consente l’avvio del noleggio. Al termine della sessione, l’utente deve poter disconnettersi dalla bicicletta: il sistema calcola automaticamente il costo della corsa, lo scala dal saldo dell’utente e rende la bicicletta nuovamente disponibile.

Dal lato amministrativo, il sistema deve consentire l’inserimento di nuove e-bike, con l’indicazione dei relativi dati tecnici (ID, modello, capacità della batteria). Deve inoltre fornire strumenti di monitoraggio che permettano agli amministratori di visualizzare lo stato complessivo della piattaforma, raccogliendo informazioni dalle biciclette e dai log delle attività degli utenti.

Vi e' inoltre comunicazione diretta tra le e-bike e il sistema centrale: ciascuna bicicletta deve inviare periodicamente informazioni sul proprio stato (ad esempio livello di batteria e posizione), in modo da mantenere aggiornati i dati disponibili agli amministratori e garantire un controllo in tempo reale.

Per garantire la qualita' del servizio, il sistema deve soddisfare due requisiti architetturali: disponibilita' e reattivita'. L'utente deve sempre essere in grado di accedere al servizio e ricevere una risposta in tempi brevi, e se non e' possibile accedere al servizio per problemi di server allora l'utente deve comunque rricevere feedback in tempi ragionevoli.

### User stories

Un modo efficacie per poter trascrivere in Gherkin questi requisiti funzionali e' attraverso le *user stories*, che sono molto simili al modello di test di Gherkin.

Di seguito sono riportati gle user stories derivate dai requisiti:

```
As a user without account
I want to create a new user account
So that i can start using the service
```
```
As a user
I want to log into my account
So that i can start using the service
```
```
As a user
I want to connect to a ebike
So that i can rent that ebike
```
```
As a user
I want to disconnect from a ebike
So that i can stop the rent of that ebike
And i can rent another ebike again
```
```
As a user
I want to recharge my credit
So that i can continue using the service
```
```
As an administrator
I want to add an ebike
So that i can make that ebike available to the users
```
```
As an administrator
I want to monitor the state of the system
So that i can run analysis on the system’s state
```
```
As an ebike
I want to periodically notify the service about my state
So that the service knows my current state
```

### Scenarios

Dalle user stories si possono derivare degli *scenarios*, che cercano di catturare al meglio le possibili interazioni che le entita' del sistema possono avere con il servizio.

Di seguito sono elencati i possibili scenarios del sistema:

```
User account creation

Main Success Scenario:
1. User requests to create a new account
2. User provides a unique username and a password
3. System creates a new account with 0 credits
4. System automatically logs the user in
5. System sends to the user a session token related to the new account

Extensions:
    2a: User provides a username already used and a password
        1. System cancels the operation and informs the user
```
```
User login

Main Success Scenario:
1. User requests to log into their account
2. User provides a valid username and password
3. System authenticates the credentials
4. System sends the user a session token and grants access to the account

Extensions:
    2a: User provides an incorrect username or password
        1. System denies access and displays an error message
```
```
User connection to an Ebike

Main Success Scenario:
1. User requests to connect to an ebike
2. User provides ID
3. System verifies the user has enough credits
4. System verifies the ebike is not connected to another user
5. System registers the connection between the user and the ebike
6. User starts using the ebike

Extensions:
    2a: User provides a wrong ebike ID
        1. System denies the connection and displays an error message
    3a: User has not enough credits
        1. System denies the connection and informs the user
    4a: Ebike is already used by another user
        1. System denies the connection and informs the user
```
```
User disconnection from an Ebike

Main Success Scenario:
1. User requests to disconnect from the currently connected ebike
2. System verifies the user’s current connection to the ebike
3. System computes the price for the rental and deducts it from the user credits
4. System registers the disconnection and ends the rental session
5. System makes the ebike available for other users to rent

Extensions:
    2a: User is not connected to this or any ebike
        1. System denies the operation and informs the user
```
```
User credits recharge

Main Success Scenario:
1. User requests to recharge their account credit
2. User provides a recharge amount
3. System processes the payment
4. System adds the selected amount to the user’s account balance

Extensions:
    2a: User provides an invalid recharge amount
        1. System denies the operation and displays an error message
```
```
Add ebike

Main Success Scenario:
1. Administrator requests to add a new ebike
2. Administrator provides the required details for the new ebike (ID, model, battery capacity)
3. System makes the ebike available for users to connect to and rent

Extensions:
    2a: Administrator provides an ebike already present in the system
        1. System denies the operation and displays an error message
```
```
Monitor the state of the system

Main Success Scenario:
1. Administrator requests to see the system’s state
2. System collects data from all ebikes and user activity logs and sends them to the administrator
3. Administrator reviews the state and conducts analysis
```
```
Periodic notification of ebike state

Main Success Scenario:
1. Ebike periodically sends its current state to the system (location, battery level)
2. System receives the data
3. System updates the ebike’s status for administrators

Extensions:
    1a: Ebike sends data in an unexpected format
        1. System ignores message
```

### Quality Attributes

Le user stories e gli scenarios riescono a descrivere adeguatamente i requisiti funzionali del servizio, tuttavia non reiscono a esprimere le qualita' architetturali che il sistema deve soddisfare per poter funzionare correttamente. Il sistema, come da requisiti, deve essere disponibile e reattivo. Per poter testare queste proprieta', si sono scritti dei *Quality Attribute Scenarios*:

| Quality Attribute: | Availability |
| ------ | ------ |
| Source | Server hosting a service |
| Stimulus | Server fails |
| Artifact | Server |
| Environment | Normal operation |
| Response | Server restarts |
| Response measure | Service downtime less than 1 min |

| Quality Attribute: | Responsiveness |
| ------ | ------ |
| Source | Server hosting a service |
| Stimulus | Server receives a request |
| Artifact | Server |
| Environment | Overloaded operation |
| Response | Server replies (with correct values or with error) |
| Response measure | Request is managed in less than 5 sec |

## Codifica in Gherkin

Partendo dalgi appena definiti scenarios, user stories e QAS, si possono ora definire utilizzando *Gherkin* i requisiti del servizio.

Per ogni user story e' stato creato un file separato che corrisponde a una feature di Gherkin, e all'interno di essa sono contenuti gli scenarios inerenti a quella user story, mostrati con le possibili variazioni. Si sono descritti con Gherkin anche i quality attributes.

I file generati sono i seguenti:
 - [QAS-Availability.feature](./feature/QAS-Availability.feature)
 - [User-Login.feature](./feature/User-Login.feature)
 - [User-ConnectToEBike.feature](./feature/User-ConnectToEBike.feature)
 - [User-DisconnectFromEBike.feature](./feature/User-DisconnectFromEBike.feature)
 - [User-CreditRecharge.feature](./feature/User-CreditRecharge.feature)

 - [Admin-AddEBike.feature](./feature/Admin-AddEBike.feature)
 - [Admin-MonitorEBike.feature](./feature/Admin-MonitorEBike.feature)

 - [EBike-PeriodicNotification.feature](./feature/EBike-PeriodicNotification.feature)
 
 - [QAS-Availability.feature](./feature/QAS-Availability.feature)
 - [QAS-Responsiveness.feature](./feature/QAS-Responsiveness.feature)

## Conclusioni

L’analisi del progetto è stata condotta adottando un approccio Domain-Driven, che si è rivelato particolarmente efficace per la definizione dei requisiti. In questo contesto, la modellazione orientata al dominio ha permesso di identificare con chiarezza le user stories, le quali si sono tradotte in modo quasi immediato nelle corrispondenti feature Gherkin. L’utilizzo di tale metodologia ha reso il passaggio dalla descrizione narrativa del problema alla formalizzazione dei requisiti testabili semplice e naturale.

Un ulteriore risultato significativo riguarda l’applicazione del linguaggio Gherkin anche ai quality attributes, ossia ai requisiti non funzionali. Attraverso la definizione di scenari mirati, è stato possibile descrivere e successivamente testare proprietà architetturali del sistema, come disponibilità e tempi di risposta, trasformandole in feature verificabili.

Gherkin si conferma quindi uno strumento estremamente potente, poiché consente di esprimere in linguaggio naturale i requisiti del sistema, mantenendo una forma comprensibile ai diversi stakeholder e al tempo stesso traducibile in test automatici. A differenza degli unit test, esso permette di operare a un livello di astrazione più alto, focalizzato sugli obiettivi funzionali e non funzionali del sistema. È tuttavia fondamentale accompagnare la definizione delle feature con la descrizione dettagliata degli scenarios, che rappresentano i veri acceptance test e garantiscono la validazione dei requisiti.