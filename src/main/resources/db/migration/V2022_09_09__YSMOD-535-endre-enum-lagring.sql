UPDATE behandling SET behandlingstype = 'VEILEDNING' where behandlingstype = '1';
UPDATE behandling SET behandlingstype = 'JOURNALFOERING' where behandlingstype = '0';
UPDATE behandling SET behandlingstype = 'VEILEDNING' where behandlingstype = '2';
UPDATE behandling SET status = 'IKKE_PAABEGYNT' where status = '1';
UPDATE behandling SET status = 'FERDIG' where status = '0';
UPDATE behandling SET status = 'UNDER_BEHANDLING' where status = '0';
UPDATE behandling SET framdriftsstatus = 'IKKE_PAABEGYNT' where framdriftsstatus = '0';
UPDATE behandling SET framdriftsstatus = 'UNDER_ARBEID' where framdriftsstatus = '1';
UPDATE behandling SET framdriftsstatus = 'PAA_VENT' where framdriftsstatus = '2';
UPDATE behandling SET framdriftsstatus = 'AVVENTER_SVAR' where framdriftsstatus = '3';
UPDATE sak SET saksstatus = 'AAPEN' where saksstatus = '0';
UPDATE sak SET sakstype = 'MENERSTATNING' where sakstype = '0';