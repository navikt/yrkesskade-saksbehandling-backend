ALTER TABLE behandling ADD COLUMN endret_tidspunkt TIMESTAMP;

-- Vi har ikke tidspunktet for siste endring, så bruker opprettet tidspunkt for å sette en verdi på behandlinger som er endret
UPDATE behandling SET endret_tidspunkt = opprettet_tidspunkt where status <> 'IKKE_PAABEGYNT';
