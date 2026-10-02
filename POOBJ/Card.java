
/**
 * Représente une carte à jouer unique définie par son rang.
 */
public class Card {
    
    // Le rang de la carte (1 pour As, 11 pour Valet, 12 pour Dame, 13 pour Roi)
    private final int rank;
    
    // Noms littéraux des rangs pour l'affichage
    private static final String[] stringRankNames = {"zéro", "as", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix", "valet", "dame", "roi"};

    /**
     * Constructeur de la carte.
     * @param rank Le rang de la carte (entier de 1 à 13).
     */
    public Card(int rank) {
        this.rank = rank;
    }

    /**
     * @return Le nom de la carte sous forme de chaîne de caractères (ex: "as", "roi").
     */
    public String toString() {
        return stringRankNames[rank];
    }

    /**
     * Calcule la valeur minimale de la carte pour le score.
     * @return 1 pour un As, 10 pour les figures (V, D, R), ou la valeur du rang sinon.
     */
    public int minValue() {
        if (rank == 1) return 1;
        else if (rank >= 10) return 10;
        else return rank;
    }

    /**
     * @return Vrai si la carte est un As, faux sinon.
     */
    public boolean isAnAce() {
        return this.rank == 1;
    }
    
} // end class Card

