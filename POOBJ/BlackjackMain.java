/**
 * Classe principale (Point d'entrée) gérant le menu global, la configuration
 * des joueurs et le lancement des sessions de jeu.
 * Cette classe permet de basculer entre un mode de jeu interactif (humains/IA)
 * et un mode de simulation statistique pour tester des stratégies.
 */
public class BlackjackMain { 

    /**
     * Action : Initialise les interfaces utilisateurs, gère la boucle principale du menu
     *          et configure les paramètres de jeu (joueurs, soldes, sabot, coefficients).
     * Le menu propose trois options :
     * 1. Jeu interactif avec affichage détaillé.
     * 2. Simulation automatique avec interface réduite.
     * 3. Sortie du programme.
     */ 
    public static void main(String[] args) {
	// Appel à l'intelligence artificielle pour pré-calculer les probabilités/espérances
  
	IA.computeGainExpectedIfDraws();

        // Initialisation de deux interfaces : ui (standard/verbeuse) et uj (silencieuse pour les tests)
        UserInterface ui = new UserInterface();
        UserInterface uj = new UserInterface(false);
        
        int nbPlayers;                   // Nombre de joueurs participant à la session
        int decksNumber;                 // Nombre de paquets de 52 cartes dans le sabot (sabot)
        double coefBlackjack = 3.1415;   // Multiplicateur de gain en cas de Blackjack (ex: 3.0)
        double initialBalance = 3.1415;  // Valeur sentinelle pour le solde initial
        Player[] players;                // Tableau stockant les instances de joueurs
        int choice;                      // Choix de l'utilisateur dans le menu principal

        do {
            // Affichage du menu principal via l'interface utilisateur
            ui.displayMessage("Bienvenue au jeu du blackjack !");
            ui.displayMessage("----------------------------------------");
            ui.displayMessage("Tu peux : ") ;
            ui.displayMessage(" 1 : jouer au Blackjack (joueurs humains ou ordinateur)") ;
            ui.displayMessage(" 2 : tester l'efficacité de la stratégie") ;    
            ui.displayMessage(" 3 : terminer") ;         
	    choice = ui.askForGeneralChoice();

            if (choice < 3) {                                                 
                
                // --- CAS 1 : JEU STANDARD INTERACTIF ---
                if (choice == 1) {
            
		    // Message d'information sur le comportement des joueurs "ordinateur"
		    ui.displayMessage("Pour un joueur ordinateur, ses choix seront affichés,");
		    ui.displayMessage("vous devrez taper sur la touche entrée pour continuer.");
		    ui.displayMessage("Il va miser 1 € à chaque partie (ou son solde si celui-ci est inférieur à 1 €).");

		    nbPlayers = ui.askForPlayersNumber();
                    decksNumber = ui.askForDecksNumber();
                    players = new Player[nbPlayers];
                    
                    // Configuration de chaque joueur individuellement
                    for (int i = 0; i < nbPlayers; i++) {
                        ui.displayMessage("\nJoueur " + (i+1)) ;    
                        boolean isHuman = ui.askForBeingHuman();
                        initialBalance = ui.askForStartingBalance(); // Saisie du solde de départ
                        players[i] = new Player(i+1, isHuman, initialBalance, ui);
                    }
                    coefBlackjack = 3.0; // Coefficient par défaut du mode interactif
                }
                
                // --- CAS 2 : TEST DE STRATÉGIE (SIMULATION) ---
                else {
                    nbPlayers = 1;      // Un seul joueur (ordinateur)
                    decksNumber = 8;    // Sabot standard de 8 paquets
                    players = new Player[1];
                    
                    // Affichage des règles spécifiques à la simulation
                    ui.displayMessage("Tu es le joueur ordinateur.") ;
                    ui.displayMessage("Tu vas miser 1 € à chaque partie (ou ton solde si celui-ci est inférieur à 1 €).");
                    ui.displayMessage("Tu arrêteras de jouer au premier des évènements suivants :") ;
                    ui.displayMessage("Tu n'as plus d'argent,") ;
                    ui.displayMessage("Tu as doublé ton solde initial.") ;
                    ui.displayMessage("Tu as effectué un nombre de parties égal à 10 fois ton solde initial.");
                    
                    boolean isHuman = false; // Joueur non-humain pour la simulation

                    ui.displayMessage("");
                    initialBalance = ui.askForStartingBalance();  

                    // Utilisation de l'interface silencieuse 'uj' pour ne pas encombrer la console
                    players[0] = new Player(1, isHuman, initialBalance, uj);
                    
                    // Explication des indicateurs statistiques affichés toutes les 10 parties
                    ui.displayMessage("Le programme affichera toutes les 10 parties :");
                    ui.displayMessage(" - le solde courant") ;
                    ui.displayMessage(" - le quotient du solde courant par le solde initial");
                    ui.displayMessage(" - le nombre de parties déjà effectuées ");
                    ui.displayMessage(" - le quotient du nombre de parties gagnées par le nombre de parties déjà effectuées") ;
                    
                    // Paramétrage personnalisé du coefficient de Blackjack pour l'étude statistique
                    ui.displayMessage("Le coefficient Blackjack est la somme en € que récupère un joueur");
                    ui.displayMessage("s'il a misé 1 € et gagne contre le croupier avec un Blackjack");
                    ui.displayMessage("(3 pour le mode interactif, 2,5 pour le jeu de Blackjack standard)");
                    ui.displayMessage("Vous pouvez choisir sa valeur entre 1 et 5");
                    ui.displayMessage("Le coefficient si le joueur gagne sans Blackjack est");
                    ui.displayMessage("automatiquement égal au coefficient Blackjack - 0,5");
                    ui.displayMessage("(2,5 pour le mode interactif, 2 pour le jeu de Blackjack standard)");

                    ui.displayMessage("");          
                    coefBlackjack = ui.askForCoefBlackjack();
                }
                
                // --- LANCEMENT DE LA PARTIE ---
                

                // Détermination du mode d'affichage : verbeux si choix 1, silencieux sinon
                boolean displayRounds = (choice == 1);
                UserInterface uiGame = displayRounds ? ui : uj;
                
                // Initialisation du Croupier avec son sabot
                Dealer dealer = new Dealer(new Shoe(decksNumber), uiGame);
                
                // Création et lancement du moteur de jeu
                BlackjackGame game = new BlackjackGame(players, dealer, displayRounds, uiGame, coefBlackjack, initialBalance);
                game.play();
            }      
        }  while (choice < 3); // Boucle tant que l'utilisateur ne choisit pas "terminer"
    }

} // end class BlackjackMain

