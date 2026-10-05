-- Schema des feuilles de match.
-- A executer dans mysql> AVANT de redemarrer l'application.
-- Les types reprennent exactement ceux attendus par Hibernate (ddl-auto=validate).

USE demo;

-- 1. Disponibilite des joueurs ------------------------------------------------
-- Les joueurs deja enregistres passent tous en ACTIF grace au DEFAULT.

ALTER TABLE joueur
  ADD COLUMN statut ENUM('ACTIF','BLESSE','SUSPENDU') NOT NULL DEFAULT 'ACTIF';

-- 2. Une feuille par equipe et par match --------------------------------------

CREATE TABLE feuille_match (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  match_id  INT NOT NULL,
  equipe_id INT NOT NULL,
  validee   BIT(1) NOT NULL DEFAULT b'0',
  CONSTRAINT uq_feuille UNIQUE (match_id, equipe_id),
  CONSTRAINT fk_feuille_match  FOREIGN KEY (match_id)  REFERENCES matchs(id) ON DELETE CASCADE,
  CONSTRAINT fk_feuille_equipe FOREIGN KEY (equipe_id) REFERENCES equipe(id)
) ENGINE=InnoDB;

-- 3. Joueurs retenus sur une feuille ------------------------------------------
-- L'ordre des valeurs ENUM doit rester identique a celui genere par Hibernate.

CREATE TABLE selection (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  feuille_id INT NOT NULL,
  joueur_id  INT NOT NULL,
  poste      ENUM('ATTAQUANT','DEFENSEUR','GARDIEN','MILIEU') NOT NULL,
  titulaire  BIT(1) NOT NULL,
  CONSTRAINT uq_selection UNIQUE (feuille_id, joueur_id),
  CONSTRAINT fk_selection_feuille FOREIGN KEY (feuille_id) REFERENCES feuille_match(id) ON DELETE CASCADE,
  CONSTRAINT fk_selection_joueur  FOREIGN KEY (joueur_id)  REFERENCES joueur(id)
) ENGINE=InnoDB;

-- Verification
SHOW TABLES;
DESCRIBE selection;
