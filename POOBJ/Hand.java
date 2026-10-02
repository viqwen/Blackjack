import java.util.ArrayList;

/**
 * Représente la main d'un joueur ou du croupier durant une partie.
 * Gère dynamiquement le calcul des scores (minimal et optimal).
 */
public class Hand {

    private CardSequence cards; // La séquence de cartes contenue dans la main
    private int minScore;       // Somme des points en comptant l'As pour 1
    private boolean hasAnAce;   // Indique si la main contient au moins un As
    private int bestScore;      // Meilleur score possible sans dépasser 21 (As valant 1 ou 11)
    
    /**
     * Constructeur : crée une main vide pouvant contenir jusqu'à nbCardsMax.
     * @param nbCardsMax Nombre maximum de cartes (typiquement 21 pour un joueur).
     */
    public Hand(int nbCardsMax) {
        this.cards = new CardSequence(nbCardsMax, true);
        this.minScore = 0;
        this.hasAnAce = false;
        this.bestScore = 0;
    }

    /**
     * Action : Vide la main et réinitialise les scores à zéro.
     */
    public void reset() { 
        hasAnAce = false;
        minScore = 0;
        bestScore = 0;
        cards.reset();
    }

    /**
     * @return Une représentation textuelle des cartes présentes dans la main.
     */
    public String cardsAsString() {
        return cards.toString();
    }

    /**
     * Action : Ajoute une carte à la main et met à jour les scores.
     * @param c La carte tirée du sabot.
     */
    public void addCard(Card c) {
        this.cards.addCard(c);
        minScore += c.minValue();
        bestScore = minScore;
        if (c.isAnAce()) {
            hasAnAce = true;
        }

        if (this.hasAnAce()) {
            if (minScore+10 <=21) {
                bestScore = minScore + 10;
            }
        }

        if (minScore > 21 ) {
            this.minScore=0;
            this.bestScore=0;
        }

        if (cards.getNbCards() == 2 && bestScore == 21) {
            this.bestScore = 22; // Blackjack
            this.minScore = 22;
        }
    }

    /** @return Le score minimal (As = 1). */
    public int minScore() {
        return minScore; 
    }

    /** @return Vrai si la main possède au moins un As. */
    public boolean hasAnAce(){
        return hasAnAce;
    }

    /** @return Le score optimal (As = 11 si possible). */
    public int bestScore() {
        return bestScore;
    }

} // end class Hand
