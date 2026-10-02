import java.util.Scanner;
import java.util.Locale;
import java.io.PrintStream;

/**
 * Gère toutes les interactions avec l'utilisateur (entrées/sorties).
 * Elle permet d'afficher l'état du jeu et de récupérer les décisions des joueurs,
 * qu'ils soient humains (saisie clavier) ou ordinateurs (affichage des choix auto).
 */
public class UserInterface {

    private Scanner input = new Scanner(System.in).useLocale(Locale.US);
    private PrintStream output = System.out;
    private boolean displayRounds; // Si faux, désactive les affichages (mode simulation)

    /**
     * Constructeur avec paramètre.
     * @param display Définit si les messages doivent être affichés ou non.
     */
    public UserInterface(boolean display){
        this.displayRounds = display;
    }

    /**
     * Constructeur par défaut (mode interactif activé par défaut).
     */
    public UserInterface(){
        this(true);
    }

    //----------------------------------------------------------
    // Gestion des Messages

    /**
     * Action : Affiche un message à la console uniquement si displayRounds est vrai.
     * @param message La chaîne de caractères à afficher.
     */
    public void displayMessage(String message) {
        if(this.displayRounds){
            this.output.println(message);
        }       
    }

    //----------------------------------------------------------
    // Attente entrée

    /**
     * Action : Marque une pause dans le programme en attendant que l'utilisateur 
     * appuie sur "Entrée". Utile pour suivre le déroulement du jeu de l'ordinateur.
     */
    public void askForGoingOn() {
        if(this.displayRounds){
            this.input.nextLine();        
        }   
    }

    //----------------------------------------------------------
    // Méthodes de lecture Booléenne (Oui/Non)

    /**
     * Action : Pose une question et attend une réponse "oui" ou "non".
     * pré-requis : this.displayRounds = true
     * @return true pour "oui", false pour "non".
     */
    public boolean readBoolean(String question) {
        String answer;
        do{
            this.output.print(question + " [oui/non] ? : ");
            answer = this.input.nextLine();
        }
        while(!answer.equalsIgnoreCase("oui") && !answer.equalsIgnoreCase("non"));
        return answer.equalsIgnoreCase("oui"); 
    }

    /**
     * Action : Affiche le choix booléen qu'un ordinateur a pris si this.displayRounds = true
     * @param ansComputer La décision prise par l'IA.
     * @return La décision transmise en paramètre.
     */
    public boolean answerBoolComputer(boolean ansComputer){ 
        this.displayMessage(ansComputer ? "oui" : "non");
        this.askForGoingOn();
        return ansComputer;
    }

    /**
     * Action : Demande une décision binaire, en adaptant la méthode selon la nature du joueur.
     * @param isHuman true si le joueur est humain, false sinon.
     * @param question La question à poser.
     * @param ansComputer Le choix déjà déterminé par l'IA.
     */
    public boolean askForYesOrNo(boolean isHuman, String question, boolean ansComputer) {
        if(isHuman){
            return this.readBoolean(question);
        }
        else{               
            this.displayMessage(question);
            return this.answerBoolComputer(ansComputer);
        }
    }

    //----------------------------------------------------------
    // Questions spécifiques au jeu (Booléens)

    public boolean askForBeingHuman() {
        return readBoolean("Es-tu un être humain"); 
    }

    public boolean askForDoubleBet(boolean isHuman, boolean ansComputer) {
        return askForYesOrNo(isHuman, "\nVeux-tu doubler ta mise (et tirer une unique carte)", ansComputer); 
    }

    public boolean askForInsurance(boolean isHuman, double betInsur, boolean ansComputer) {
        return askForYesOrNo(isHuman,"V\nVeux-tu t'assurer pour le quart de ta mise, soit " + betInsur + " € contre un Black Jack du croupier", ansComputer); 
    }

    public boolean askForSurrender(boolean isHuman, double bet, boolean ansComputer) {
        return askForYesOrNo(isHuman, "\nVeux-tu abandonner ce tour et récupérer la moitié de ta mise, soit " + bet/2 + " € ?", ansComputer); 
    }

    /**
     * Demande au joueur s'il veut tirer une carte (Hit) ou s'arrêter (Stand).
     * @return true si le joueur choisit Hit, false s'il choisit Stand.
     */
    public boolean askForHitOrStand(boolean isHuman, boolean ansComputer) {
        return askForYesOrNo(isHuman, "\nVeux-tu tirer une carte", ansComputer); 
    }

    //---------------------------------------------------------
    // Lecture d'Entiers (Validation de saisie)

    /**
     * Action : Lit un entier au clavier avec validation de type et de plage.
     * pré-requis : this.displayRounds = true
     */
    private int readIntBetween(String prompt, int min, int max) {
        int value;
        do {
            this.output.println(prompt + " ? (" + min + " à " + max + " ) : ");
            while (!input.hasNextInt()) {
                this.output.println("Ce n'est pas un nombre entier !");
                this.input.nextLine(); // consomme l'entrée invalide
            }
            value = this.input.nextInt();
            this.input.nextLine(); // Consomme le retour chariot
            if (value < min || value > max) {
                this.output.println("Erreur : tu dois entrer un nombre valide (entre " + min + " et " + max + ").");
            }
        } while (value < min || value > max);
        return value;
    }

    public int askForGeneralChoice() {
        return readIntBetween("Quel est ton choix", 1, 3);
    }
    
    public int askForPlayersNumber() {
        return readIntBetween("Combien de joueurs participeront au jeu", 1, 6);
    }
    
    public int askForDecksNumber() {
        return readIntBetween("Combien de jeux de 52 cartes", 1, 8);
    }

    //----------------------------------------------------------
    // Lecture de Doubles (Argent / Coefficient Blackjack)

    /**
     * Action : Lit un nombre décimal avec validation.
     * pré-requis : this.displayRounds = true
     */
    private double readDoubleBetween(String prompt, double min, double max) {
        double value;
        do {
            this.output.println(prompt + " ? (" + min + " à " + max + " ) : ");
            while (!input.hasNextDouble()) {
                this.output.println("Ce n'est pas un nombre !");
                this.input.nextLine();
            }
            value = input.nextDouble();
            this.input.nextLine(); // Consomme le retour chariot
            if (value < min || value > max) {
                this.output.println("Erreur tu dois entrer un nombre supérieur ou égal à " + min + " et inférieur ou égal à " + max);
            }
        } while (value < min || value > max);
        return value;
    }

    /**
     * Action : Affiche la mise décidée par l'ordinateur. si this.displayRounds = true
     */
    public double answerDoubleComputer(double ansComputer){
        this.displayMessage("" + ansComputer);
        this.askForGoingOn();
        return ansComputer;
    }
 
    /**
     *  pré-requis : this.displayRounds = true
     */
    public double askForStartingBalance() {
        return readDoubleBetween("Quel est ton solde", 1, 10000);
    }

    /**
     * Demande la mise pour le tour courant.
     */
    public double askForBet(boolean isHuman, int playerNum, double balance, double ansComputer) {
        if(isHuman){
            return this.readDoubleBetween("\nJoueur " + playerNum + " - solde " + balance + " - quelle est ta mise", 0.0, balance);
        }
        else{               
            this.displayMessage("\nJoueur " + playerNum + " - solde " + balance + " - quelle est ta mise ? (0.0 à " + balance + ") ");
            return this.answerDoubleComputer(ansComputer);
        }
    }
 
    /**
     * pré-requis : this.displayRounds = true
     */
    public double askForCoefBlackjack() {
        return readDoubleBetween("Quel est le coefficient Blackjack", 1, 5);
    }

    //----------------------------------------------------------
    // pour l'affichage d'une carte

    public static String unOrUneCardName(Card card){
	return (card.toString().equals("dame") ? "une " : "un ") + card.toString();
    }

    //----------------------------------------------------------

    // Remarque :Dans les méthode suivantes, comme tous les affichages se font avec 
    //   this.displayMessage, ils ne sont faits que si this.displayRounds == true

    //----------------------------------------------------------
    // Affichages liés au Dealer (Croupier)

    /**
     * Affiche l'information partielle du croupier au début du tour.
     */
    public void displayDealerUpCard(Card upCard) {
        int visibleScore = upCard.minValue(); 
        this.displayMessage("\n" + "--- 🃏 Carte Découverte du Croupier ---");
        this.displayMessage("Le Croupier a une carte visible : " + upCard.toString());
        this.displayMessage("... et une carte cachée (Hole Card).");
        this.displayMessage("Score visible : " + (upCard.isAnAce() ? 11 : visibleScore) + " points.");
        this.displayMessage("-------------------------------------" + "\n");
    }

    public void displayHandAndScoreDealer(Dealer dealer) {
        this.displayMessage(dealer.toString());
    }

    /**
     * Affiche la carte piochée par le croupier pendant son tour de jeu, ainsi que son nouveau score
     */
    public void displayCardDrawnAndScoreDealer(Card card, int bestScore) {
        this.displayMessage("Le croupier a tiré "   +  unOrUneCardName(card) + ", il a " + bestScore + " points."); 
    }

    public void displayDealerFinalScore(int score) {
        this.displayMessage("--- Score Final du Croupier : " + score + " points ---");
    }

    //----------------------------------------------------------
    // Affichages liés aux Players (Joueurs)

    /**
     * Affiche l'état complet d'un joueur.
     * @param isFinalScore : permet de déterminer si 1 ou 2 valeurs de score sont à afficher
     */
    public void displayPlayerStatus(Player p, boolean isFinalScore) {        
        this.displayMessage(p.playerToString(isFinalScore));
    }

    /**
     * Affiche la carte piochée par le joueur et son nouveau score.
     */
    public void displayCardDrawnAndScorePlayer(Card card, String scorePlayer) {
        this.displayMessage("Tu as tiré " +  unOrUneCardName(card)     + scorePlayer); 
    }
    
    /**
     * Affiche le bilan financier de la main pour un joueur.
     */
    public void displayPlayerResult(double gain, double gainInsur, double balance, double bet, boolean insurance) {   
        String message;
        if (gain == bet) message = "Égalité : tu récupères ta mise";
        else if (gain > 0) message = "Gagné ! Tu remportes " + gain + " €.";
        else message = "Perdu : tu perds ta mise !";
        this.displayMessage(message);

        if (insurance) {
            this.displayMessage("Tu t'es assuré contre un Black Jack du croupier.");
            if(gainInsur > 0){
                this.displayMessage("Le croupier a fait un Blackjack, tu récupères 2 fois ta prime d'assurance, soit " + gainInsur + " €"); 
            }
            else{
                this.displayMessage("Le croupier n'a pas fait de Black Jack, tu ne récupères rien.");
            }               
        } 
        this.displayMessage("Nouveau Solde : " + balance + " €");
        if(balance <= 0.0) {
            this.displayMessage("Tu n'as plus d'argent, tu ne peux plus jouer !");
        }
    }
    

} // end class UserInterface

