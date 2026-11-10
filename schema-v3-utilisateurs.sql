-- Comptes utilisateurs pour l'authentification par JWT.
-- A executer dans mysql> AVANT de redemarrer l'application.
-- Les types reprennent exactement ceux attendus par Hibernate (ddl-auto=validate).

USE demo;

CREATE TABLE utilisateur (
  id              INT AUTO_INCREMENT PRIMARY KEY,
  nom_utilisateur VARCHAR(50)  NOT NULL,
  -- Hash BCrypt (60 caracteres), jamais le mot de passe en clair
  mot_de_passe    VARCHAR(100) NOT NULL,
  -- L'ordre des valeurs ENUM doit rester celui genere par Hibernate (alphabetique)
  role            ENUM('ENTRAINEUR','MODERATEUR') NOT NULL,
  CONSTRAINT uq_utilisateur_nom UNIQUE (nom_utilisateur)
) ENGINE=InnoDB;

-- Le premier moderateur est cree par l'application a son demarrage,
-- a partir de r5a05.app.moderateur.nom / motDePasse dans application.properties.

DESCRIBE utilisateur;
