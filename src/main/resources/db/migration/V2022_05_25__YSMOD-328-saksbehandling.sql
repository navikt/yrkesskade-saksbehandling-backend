CREATE TABLE sak
(
    sak_id               BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    tema                 VARCHAR(50)  NOT NULL,
    sakstype             VARCHAR(50)  NOT NULL,
    saksstatus           VARCHAR(50)  NOT NULL,
    opprettet_tidspunkt  TIMESTAMP    NOT NULL,
    opprettet_av         VARCHAR(255) NOT NULL,
    bruker_identifikator VARCHAR(12)  NOT NULL -- fnr/dnr
);

CREATE TABLE behandling
(
    behandling_id                   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    tema                            VARCHAR(3)      NOT NULL,
    bruker_id                       VARCHAR(255)    NOT NULL,
    bruker_id_type                  VARCHAR(255)    NOT NULL,
    sak_id                          BIGINT,
    behandlende_enhet               VARCHAR(100)    NOT NULL, -- kode til behandlingsenhet
    saksbehandlingsansvarlig_ident  VARCHAR(255),             -- ident til saksbehandler
    behandlingstype                 VARCHAR(255)    NOT NULL,
    status                          VARCHAR(50)     NOT NULL,
    behandlingsfrist                TIMESTAMP       NOT NULL,
    journalpost_id                  VARCHAR(255)    NOT NULL,
    dokumentkategori                VARCHAR(255)    NOT NULL,
    systemreferanse                 VARCHAR(255)    NOT NULL,
    framdriftsstatus                VARCHAR(255)    NOT NULL,
    opprettet_tidspunkt             TIMESTAMP       NOT NULL,
    opprettet_av                    VARCHAR(255)    NOT NULL,
    endret_av                       VARCHAR(255),
    CONSTRAINT fk_sak_id FOREIGN KEY (sak_id) REFERENCES sak (sak_id) ON DELETE CASCADE
);

-- 1-til-1 relasjon? - En behandling kan ha ett resultat?
CREATE TABLE behandlingsresultat
(
    behandlingsresultat_id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    behandling_id             BIGINT       NOT NULL,
    resultat                  TEXT         NOT NULL,
    resultat_tidspunkt        TIMESTAMP    NOT NULL,
    sted                      VARCHAR(255) NOT NULL,
    bakgrunnsaarsak           TEXT         NOT NULL,
    utfyllende_beskrivelse    TEXT         NOT NULL,
    besluttende_saksbehandler TEXT         NOT NULL,
    CONSTRAINT fk_behandling_id FOREIGN KEY (behandling_id) REFERENCES behandling (behandling_id) ON DELETE CASCADE
);
