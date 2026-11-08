create table if not exists Eventi
(
    id           INTEGER
        primary key
        unique,
    data         TEXT,
    luogo        TEXT,
    costo_totale INTEGER,
    tipologia    TEXT,
    stato        TEXT
)
    strict;

create table if not exists Musicisti
(
    id       INTEGER not null
        primary key autoincrement
        unique,
    nome     TEXT,
    cognome  TEXT,
    email    TEXT,
    password TEXT,
    telefono TEXT,
    cachet   INTEGER,
    tipo     TEXT
)
    strict;

create table if not exists Cantanti
(
    id              INTEGER not null
        primary key
        unique
        references Musicisti
            on delete cascade,
    genere_musicale TEXT,
    registro_vocale TEXT
)
    strict;

create table if not exists Disponibilità
(
    musicista     integer not null
        constraint musicista
            references Musicisti
            on delete cascade,
    disponibilità TEXT    not null,
    constraint Disponibilità_pk
        primary key (musicista, disponibilità)
)
    strict;

create table if not exists Partecipazioni
(
    musicista  INTEGER           not null
        references Musicisti
            on delete cascade,
    evento     INTEGER           not null
        references Eventi
            on delete cascade,
    costo      INTEGER,
    confermato int default false not null,
    primary key (musicista, evento)
)
    strict;

create table if not exists Strumentisti
(
    id INTEGER not null
        primary key
        unique
        constraint id
            references Musicisti
            on delete cascade
)
    strict;

create table if not exists Strumenti
(
    strumentista INTEGER not null
        constraint id
            references Strumentisti
            on delete cascade,
    strumento    TEXT    not null,
    primary key (strumentista, strumento)
)
    strict;
