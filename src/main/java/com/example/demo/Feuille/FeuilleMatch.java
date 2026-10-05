package com.example.demo.Feuille;

import com.example.demo.Equipe.Equipe;
import com.example.demo.Matchs.Match;

import jakarta.persistence.*;

/** Feuille de match d'une equipe pour un match donne. */
@Entity
@Table(name = "feuille_match",
       uniqueConstraints = @UniqueConstraint(name = "uq_feuille", columnNames = { "match_id", "equipe_id" }))
public class FeuilleMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    private Boolean validee = false;

    public FeuilleMatch() {}

    public FeuilleMatch(Match match, Equipe equipe) {
        this.match = match;
        this.equipe = equipe;
        this.validee = false;
    }

    public Integer getId() { return id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    public Boolean getValidee() { return validee; }
    public void setValidee(Boolean validee) { this.validee = validee; }

    public boolean estValidee() { return Boolean.TRUE.equals(validee); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeuilleMatch other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return FeuilleMatch.class.hashCode();
    }

    @Override
    public String toString() {
        return "FeuilleMatch{id=" + id
                + ", matchId=" + (match != null ? match.getId() : null)
                + ", equipeId=" + (equipe != null ? equipe.getId() : null)
                + ", validee=" + validee
                + '}';
    }
}
