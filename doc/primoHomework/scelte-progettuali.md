# Scelte progettuali

Il progetto riguarda la gestione di un ospedale, in particolare dei letti, dei ricoveri e dei turni dei medici. In questo primo homework i dati sono tenuti in memoria. Essendo un gruppo di due persone, non è prevista la parte sulla malattia e sui medici sostitutivi.

## Classi principali

`Utente` contiene login e password. Da questa classe derivano `Amministratore` e `Medico`, che hanno le stesse informazioni per l'accesso ma svolgono compiti diversi. L'amministratore si occupa dei pazienti e dei ricoveri, mentre il medico delle prestazioni e dei loro esiti. Il medico ha anche una matricola e appartiene a un solo reparto.

La struttura è rappresentata da `Reparto`, `Stanza` e `Letto`. Un reparto contiene più stanze e ogni stanza contiene dei letti. Il numero della stanza è univoco nel reparto, mentre il codice del letto deve essere diverso in tutto l'ospedale. In questo modo due reparti possono avere entrambi una stanza numero 1, ma non due letti con lo stesso codice.

`Paziente` contiene codice fiscale, nome e cognome. `Ricovero` collega il paziente al letto e conserva le date di ingresso e di dimissione. Le prestazioni sono rappresentate da `Prestazione`, che contiene il tipo, gli orari e l'esito. Ogni prestazione è collegata al medico che la esegue e al ricovero del paziente.

La classe `Ospedale` raccoglie i dati e permette di fare i controlli che riguardano più oggetti. Per esempio, quando si inserisce un ricovero bisogna controllare anche quelli già presenti sullo stesso letto.

## Collegamenti nel diagramma

Tra reparto e stanza e tra stanza e letto è stata usata la composizione, perché ogni stanza appartiene a un reparto e ogni letto a una stanza. Gli elenchi possono essere vuoti all'inizio, quando la struttura deve ancora essere completata.

Un paziente può avere più ricoveri nel tempo. Anche un letto può essere usato per più ricoveri: il limite riguarda la sovrapposizione degli orari, non il numero totale di ricoveri. Un medico può avere più turni e più prestazioni, ma afferisce a un solo reparto.

Nel diagramma i collegamenti non sono ripetuti anche come attributi. `TestModel` non è presente perché serve solo a provare il funzionamento delle classi.

## Controlli sui ricoveri

Prima di inserire un ricovero, il paziente e il letto devono essere già registrati. Il sistema controlla che il letto sia libero per tutto il periodo e che il paziente non abbia già un altro ricovero negli stessi orari.

La fine del ricovero è la dimissione effettiva, se presente; altrimenti viene usata quella prevista. Quindi, se la permanenza si prolunga, bisogna aggiornare la data prevista. Senza questo aggiornamento il letto risulta libero dopo la scadenza prevista.

L'inizio è incluso nel periodo di occupazione, la fine è esclusa. Per esempio, se un paziente viene dimesso alle 10:00, un altro può occupare lo stesso letto dalle 10:00. Il controllo della sovrapposizione è:

```text
inizio1 < fine2 e inizio2 < fine1
```

Gli stessi controlli vengono eseguiti quando si modifica un ricovero. Se uno dei controlli fallisce, i dati non vengono cambiati. Non è possibile modificare le date lasciando fuori dal ricovero una prestazione già registrata. Il cambio di letto riguarda l'intero ricovero e non conserva lo storico dei trasferimenti.

## Turni e prestazioni

`Turno` contiene il giorno della settimana e le ore di inizio e fine. I turni si ripetono ogni settimana e possono proseguire dopo mezzanotte: per esempio, domenica dalle 22:00 alle 06:00 del lunedì. Non sono ammessi turni con inizio e fine uguali o turni sovrapposti dello stesso medico.

Una prestazione può essere una visita o un intervento. Deve iniziare e finire durante il ricovero del paziente e all'interno di un singolo turno del medico. Non possono esserci due prestazioni dello stesso medico negli stessi orari. Possono invece esserci prestazioni contemporanee svolte da medici diversi.

L'esito parte da `In attesa di esito` e può essere scritto o modificato dal medico della prestazione dopo che questa è terminata.

## Gestione dei dati

Gli attributi sono privati. Per gli elenchi sono usate `List` e `ArrayList`; i getter restituiscono una copia della lista per evitare modifiche esterne ai collegamenti. Gli identificativi, come codice fiscale e codice del letto, non hanno setter. Sono univoci anche il nome del reparto e la matricola del medico. Per questi confronti non si distinguono maiuscole e minuscole.

Login e password sono entrambe univoche, come indicato nella traccia. Per le credenziali si distinguono maiuscole e minuscole e la password non ha un getter pubblico. Il codice fiscale deve essere non vuoto, ma non viene controllato il suo formato completo.

Il modello permette di cercare i letti liberi, consultare l'agenda giornaliera o settimanale e vedere i pazienti con dimissione prevista in una data, escludendo quelli già dimessi. Le agende sono ordinate per inizio e la settimana parte dal lunedì. Sono comprese anche le prestazioni iniziate il giorno prima che proseguono nel periodo cercato.

I dati non validi vengono segnalati con `IllegalArgumentException`. Interfaccia grafica, sessione utente e database saranno aggiunti negli homework successivi.
