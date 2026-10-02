import java.util.Random;

/**
 * Structure de données de base stockant une suite de cartes.
 * Utilisée à la fois pour les mains (Hand) et pour le sabot (Shoe).
 */
public class CardSequence {
    
    private Card[] seq;         // Tableau de stockage des objets Card
    private int nbCards;        // Nombre effectif de cartes actuellement dans la séquence
    private boolean isInAHand;  // Distingue le comportement (Main vs Sabot)

    public static Random random = new Random();

    /**
     * Pré-requis : nbCardsMax >= 0
     * @param nbCardsMax Capacité maximale du tableau.
     * @param isInAH Indique si la séquence appartient à une main (true) ou un sabot (false).
     */
    public CardSequence(int nbCardsMax, boolean isInAH){
        this.seq = new Card[nbCardsMax];
        this.isInAHand = isInAH;
    }

    /**
     * Action : Réinitialise la séquence.
     * Si c'est une main, elle devient vide.
     * Si c'est un sabot, elle redevient pleine et est mélangée.
     */
    public void reset(){
        if (this.isInAHand) {
            seq = new Card[this.seq.length];
            nbCards = 0;
        }

        else {
            nbCards = seq.length;
            int nbDeck= nbCards/52;
            nbCards = 0;
            for (int i=0;i<nbDeck;i++) {
                for (int r=1; r<=13; r++){
                    for (int color = 0; color < 4; color++) { // Pour les 4 couleurs
                        seq[nbCards] = new Card(r); 
                        nbCards++;
                    }
                }
            }
            shuffleCards();
        }
    }

    /**
     * @return Chaîne listant le nom de toutes les cartes présentes.
     */
    public String toString(){
        String res="";
        for (int i=0; i<nbCards; i++){
            res += seq[i].toString() + " ";
        }
        return res;
        
    }

    /**
     * Pré-requis : this.nbCards < this.seq.length
     * Action : Ajoute une carte à la fin de la séquence.
     * @param newCard La carte à ajouter.
     */
    public void addCard(Card newCard){
        seq[nbCards] = newCard;
        nbCards++;
    }

    /**
     * Pré-requis : this.nbCards > 0
     * Action : Retire et renvoie la dernière carte de la séquence.
     * @return La carte retirée.
     */
    public Card removeCard(){
        nbCards--;
        Card c = seq[nbCards];
        seq[nbCards] = null;
        return c;
    }
    
    /**
     * Pré-requis : this.isInAHand = false
     * Action : Mélange aléatoirement les cartes du tableau (algorithme de permutation).
     */
    public void shuffleCards(){
        for (int i = nbCards - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Card temp = seq[i];
            seq[i] = seq[j];
            seq[j] = temp;
        }
    }

    public int getNbCards() {
        return nbCards;
    }

}

