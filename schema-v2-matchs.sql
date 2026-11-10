-- Evolution du schema : date programmee, statut de match, suppression logique.
-- A executer dans mysql> AVANT de redemarrer l'application.
-- Les types reprennent exactement ceux attendus par Hibernate (ddl-auto=validate).

USE demo;

-- 1. Date programmee et cycle de vie du match --------------------------------
-- datetime(6) et non datetime : c'est la precision qu'Hibernate attend pour LocalDateTime.
-- L'ordre des valeurs ENUM doit rester celui genere par Hibernate (alphabetique).

ALTER TABLE matchs
  ADD COLUMN date_prevue DATETIME(6) NULL,
  ADD COLUMN statut ENUM('ANNULE','JOUE','PROGRAMME') NOT NULL DEFAULT 'PROGRAMME';

-- 2. Suppression logique des equipes et des joueurs ---------------------------
-- La ligne n'est jamais effacee : les matchs et feuilles de match gardent leurs liens.

ALTER TABLE equipe ADD COLUMN supprime BIT(1) NOT NULL DEFAULT b'0';
ALTER TABLE joueur ADD COLUMN supprime BIT(1) NOT NULL DEFAULT b'0';

-- 3. Reprise des donnees existantes -------------------------------------------
-- Les matchs deja crees n'ont pas de date : on leur en donne une.
-- Ceux qui portent deja un resultat sont consideres comme joues.

UPDATE matchs SET date_prevue = '2026-04-12 21:00:00' WHERE date_prevue IS NULL;
UPDATE matchs SET statut = 'JOUE' WHERE resultat_equipe1 IS NOT NULL;

-- Verification
SELECT id, equipe1_id, equipe2_id, date_prevue, statut, resultat_equipe1, resultat_equipe2 FROM matchs;
