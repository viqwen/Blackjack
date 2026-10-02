/**
 * Classe Contrôleur qui orchestre le déroulement d'une session de Blackjack.
 * Elle gère l'enchaînement des phases (mises, distribution, tours, résultats)
 * et supervise le mode simulation pour tester la stratégie de l'IA.
 *
 * Remarque sur les scores particuliers
 * -------------------------------------------
 * Un Blackjack est représenté par le score 22.
 * Un dépassement de 21 points est représenté par le score 0.
 * Dans les tableaux de l'IA, ces deux valeurs sont utilisées pour le croupier
 * comme pour le joueur.
 * Pour le jeu, le score est le nombre réel de points obtenus par le croupier
 * ou le joueur : un Blackjack est représenté par le score 21 et l'attribut hasBlackjack
 * égal à true, et un dépassement de 21 points par ce même score dépassant 21 points.
 */

public class BlackjackGame {

    private Player[] players;       // Tableau des joueurs
    private Dealer dealer;          // Le croupier (la banque)≈Ò
    private int nbActive;           // Nombre de joueurs encore dans le jeu
    private boolean displayRounds;  // true : mode interactif, false :  mode simulation (test de la stratégie)
    private UserInterface ui;
    private double coefBlackjack;   // Multiplicateur de gain pour un Blackjack
    
    // Variables dédiées au suivi statistique (mode simulation)
    private double initialBalance;
    private double currentBalance;
    private int nbRounds;
    private int nbWinningRounds;


    // --- Constructeur ---

    /**
     * Initialise une nouvelle session de jeu.
     * pré-requis : 1 <= coefBlackjack <= 5 et 1 <= initialBalance <= 10000
     */
    public BlackjackGame(Player[] players, Dealer dealer, boolean displayRounds, UserInterface ui,
                         double coefBlackjack, double initialBalance) {
        if (coefBlackjack < 1 || coefBlackjack > 5) {
            throw new IllegalArgumentException("Le coefficient de Blackjack doit être entre 1 et 5.");
        }
        if (initialBalance < 1 || initialBalance > 10000) {
            throw new IllegalArgumentException("Le solde initial doit être entre 1 et 10 000.");
        }
        this.players = players;
        this.dealer = dealer;
        this.displayRounds = displayRounds;
        this.ui = ui;
        this.coefBlackjack = coefBlackjack;
        this.initialBalance = initialBalance;
        this.currentBalance = initialBalance;
        this.nbRounds = 0;
        this.nbWinningRounds = 0;
        this.nbActive = players.length;
	
    }

    /**
     * Action : Remet à zéro la main du croupier et de chaque joueur actif 
     *          avant de commencer un nouveau tour.
     */
    public void reset() {
        for (Player p : this.players) {
            if(p.active()){
                p.reset();
            }
        } 
        this.dealer.reset();
    }
  
    /**
     * Action : Sollicite chaque joueur pour sa mise.
     * Si un joueur mise 0, il est retiré des joueurs actifs.
     */
    public void collectBets() {
        for (Player p: this.players) {
            if (p.active()) {
                if (p.eliminatedWhenCollectingBet()) {
                    nbActive--;
                }
            }
        }
    }

    /**
     * Action : Distribue les deux premières cartes à tout le monde.
     * @return La première carte visible du croupier (Up-Card).
     */
    public Card dealInitialCards() {
        Card c;
        for (int i = 0; i < 2; i++) {
            
            for (Player p : this.players) {
                if (p.active()) {
               
                    c = this.dealer.drawCard();
    
                    p.takeCard(c);
                }
            }
            
        }
            Card upC = this.dealer.drawCard();
            this.dealer.takeCard(upC);
            c= this.dealer.drawCard();
            this.dealer.takeCard(c);

        return upC;
    
    }

    /**
     * Action : Affiche l'état initial du tour (état du jeu des joueurs et carte visible du croupier).
     */
    public void displayAllVisibleCards(Card upCard) {
        for (Player p : this.players) {
            if (p.active()) {
                ui.displayPlayerStatus(p, false);
            }
        }
        ui.displayDealerUpCard(upCard);
    }

    /** Action : Gère le tour de parole de chaque joueur, puis celui du croupier.
     *  @param upCardMinValue Valeur de la carte visible du croupier pour la stratégie et l'assurance.
     */
    public void playTurns(int upCardMinValue) {
        for (Player p : this.players) {
            if (p.active()) {
                p.playTurn(dealer, upCardMinValue);
            }
        }
        this.dealer.playTurn();
    }

    /**
     * Action : Orchestre le calcul des gains (Modèle) et leur affichage (Vue).
     * @return Le solde du dernier joueur traité (utile pour la simulation mono-joueur).
     */
  
    public double processAndDisplayResults() {
        this.ui.displayDealerFinalScore(this.dealer.bestScore());
        
        double lastBalance = 0.0;
        
        for (Player p : this.players) {
            if (p.active()) {
                lastBalance = p.processAndDisplayResult(this.dealer, this.coefBlackjack);
                
                if (!p.active()) {
                    this.nbActive--;
                }
            }
        }
        
        return lastBalance;
    }

    /**
     * Action : Détermine si le jeu doit s'arrêter.
     * En mode interactif : s'arrête si plus de joueurs actifs.
     * En mode simulation : délègue à (appelle) roundsSimulation.
     * @return true si une nouvelle manche peut commencer, false sinon
     */
    public boolean endOfRound(double balance) {
        if (this.displayRounds) {
            return this.nbActive > 0;
        } 
        return this.roundsSimulation(balance);
	
    }

    /**
     * Utilitaire pour arrondir un montant à 2 décimales (format monétaire).
     */
    public static double round2digits(double x){ 
        return ((double) Math.round(x * 100)) / 100;
    }

    /** Logique de test de l'IA : Calcule les statistiques de performance de la stratégie.
     *  Conditions d'arrêt : Faillite, Solde doublé, ou limite de 10 x le solde en nombre de parties.
     *  @return true si une nouvelle partie peut commencer, false sinon.
     */
    public boolean roundsSimulation(double balance) {

    this.nbRounds++;

    if (this.nbRounds % 10 == 0) {
        System.out.println("Tour " + nbRounds + " | Solde : " + round2digits(balance) + 
                           " | Ratio : " + round2digits(balance/initialBalance));
    }

    if (balance <= 0 || balance >= 2 * this.initialBalance || this.nbRounds >= 10 * this.initialBalance) {
        return false;
    }
    return true;
}

    /**
     * Action : Déroule un cycle complet d'une partie.
     * @return true si une nouvelle partie peut commencer, false sinon.
     */
    public boolean playRound() {
        this.reset();
        this.ui.displayMessage("\n--- Nouvelle partie : Réinitialisation effectuée ---\n");

        this.ui.displayMessage("Collecte des mises...");
        this.collectBets();
        
        if(nbActive == 0){
            this.ui.displayMessage("Aucun joueur actif pour commencer la partie.");
            return false;
        }
        
        this.ui.displayMessage("Distribution des cartes...");
        Card upCard = this.dealInitialCards();

        this.displayAllVisibleCards(upCard);
        
        this.ui.displayMessage("Tours des joueurs et du croupier...");
        this.playTurns(upCard.minValue());
        
        this.ui.displayMessage("\n--> Résultats de la partie <--\n");
        double balance = this.processAndDisplayResults(); 
        
        return this.endOfRound(balance);
    }

    /**
     * Point d'entrée pour lancer la session de jeu.
     * Boucle tant que les conditions de fin (endOfRound) ne sont pas remplies.
     */
    public void play() {
        this.ui.displayMessage("\n    PREMIÈRE PARTIE");
        while (this.playRound()) { 
            this.ui.displayMessage("\n    NOUVELLE PARTIE ?");
        } 
    }

} // end class BlackjackGame

