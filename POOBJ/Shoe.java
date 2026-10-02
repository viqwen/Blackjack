import java.util.ArrayList;
import java.util.Collections;

/**
 * Représente le sabot (ensemble de cartes) utilisé par le croupier.
 * Remarque :
 * "deck" désigne ici un paquet de 52 cartes, et non le sabot complet.
 */

public class Shoe { 

    private CardSequence cards;   // Ensemble des cartes disponibles
    private int numberOfDecks;    // Nombre de paquets de 52 cartes utilisés
    
    /**
     * Constructeur : Remplit le sabot avec n paquets de 52 cartes.
     * @param n Nombre de paquets (entre 1 et 8).
     */
    public Shoe(int n) {
        this.numberOfDecks = n;
        this.cards = new CardSequence(52 * numberOfDecks, false);
        this.cards.reset();
    }

    /**
     * Action : Remet toutes les cartes initiales dans le sabot et les mélange.
     */
    public void reset() {
        this.cards.reset();
    }
    
    /**
     * Action : Retire la carte du dessus du sabot.
     * @return La carte tirée.
     */
    public Card drawCard() {
        return this.cards.removeCard();
    }

    public int getNbofDecks() {
        return this.numberOfDecks;
    }

} // end class Shoe
