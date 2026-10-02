import java.util.Random;
import java.util.Scanner;
import java.util.Locale;
import java.io.PrintStream;

public class ExtendedBlackjack {

    public static Scanner input = new Scanner(System.in).useLocale(Locale.US);
    public static PrintStream output = System.out;
    public static Random random = new Random();



    public static int cardsNumber(int[] T) {
	return T[0];
    }

    public static int[] generateCards(int n) {
        int total = 52 * n;
        int[] paquet = new int[total + 1];
        paquet[0] = total;

        for (int i=1; i < total+1; i++){
            paquet[i] = ((i - 1) % 13) +1;
        }
        return paquet;
    }

    public static void shuffleCards(int[] deck) {
        Random rand = new Random();
        int n = deck[0];
        for (int i = n; i > 1; i--){
            int j = rand.nextInt(i) + 1;
            int memory = deck[i];
            deck[i] = deck[j];
            deck[j]= memory;
        }
    }

    public static String cardName(int card) {
        String name;
        switch (card) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                name = String.valueOf(card);
                break;
            case 1:
                name = "as";
                break;
            case 11:
                name = "valet";
                break;
            case 12:
                name = "dame";
                break;
            case 13:
                name = "roi";
                break;
            default:
                name = "Erreur CardName ce n'est pas une carte card doit etre compris entre 1 et 13";
        }
        return name;
    }

    public static int drawCard(int[] deck, int[] hand) {

        int card = deck[deck[0]];
        deck[deck[0]]=0;
        deck[0]--;
        hand[0]++;
        hand[hand[0]]=card;
        return card;

    }

    public static double[][] playGame(int nbPlayer, int nbPacks) {
        double[] money= new double[nbPlayer];
        boolean[] active=new boolean[nbPlayer];
        double[] bet=new double[nbPlayer];
        double[][] résultat= new double[nbPlayer][2];

        for (int i=0;i<nbPlayer;i++) {

            active[i]=true;
            bet[i]=0;
            output.printf("Donner la somme en Euros que possède le joueur %s (entre 1.0 et 1000000.0) : ", i + 1);
            money[i] = toDouble(input.next());
            while (money[i] < 1 || money[i] > 1000000) {
                output.printf("Réponse incorrecte ! %n");
                output.printf("Donner la somme en Euros que possède le joueur %s (entre 1.0 et 1000000.0) : ", i + 1);
                money[i] = toDouble(input.next());
            }
            résultat[i][0]=money[i];
        }

        output.print('\n');
        int[] paquet;
        int nbTurn=0;
        do {

            if (nbTurn == 0) {

                output.printf("PREMIÈRE PARTIE%n%n");
            } else {

                output.printf("NOUVELLE PARTIE ?%n%n");
            }
            nbTurn++;
            paquet = generateCards(nbPacks);
            shuffleCards(paquet);
        } while ( playRound(active,money,paquet) );

        output.println("Et le combat cessa faute de combattants.");
        output.print('\n');

        for (int i=0;i<nbPlayer;i++) {
            résultat[i][1]= money[i];
            if (résultat[i][0] < money[i]) {
                output.printf("Joueur %s: tu as gagné %s €.%n",i+1,money[i]-résultat[i][0]);

            }
            else if (résultat[i][0]>money[i]) {
                output.printf("Joueur %s: tu as perdu %s €.%n",i+1,résultat[i][0]-money[i]);

            }
            else {
                output.printf("Joueur %s: tu as rien gagner ou perdu.%n",i+1);
            }
        }


        return résultat;

    }

    public static boolean playRound(boolean[] active, double[] money, int[] deck) {


        double[] bet = new double[active.length];

        int[][] playerHand =new int[active.length][23];
        int[] dealerHand = new int[23];
        int[] PlayerScore = new int[active.length];
        boolean[] pInsured = new boolean[active.length];
        collectBets(active,money,bet);

        if (!playersRemain(active)) {
            return false;
        }
        dealInitialCards(active, playerHand, dealerHand, deck);

        displayGameInit(active,money,bet,playerHand,dealerHand[1]);

        int DealerScore = playTurn(active,money,bet,playerHand,dealerHand,PlayerScore,deck,pInsured);

        output.printf("--> Résultats de la partie <--%n%n");

        output.printf("Le croupier a %s points.%n%n",handvalue(dealerHand,false));

        processAndDisplayResults(active,money,bet,playerHand,PlayerScore,DealerScore,pInsured);

        return playersRemain(active);

    }

    public static boolean playersRemain(boolean[] active) {
        for (int i = 0; i < active.length; i++){
            if (active[i]){
                return true;
            }
        }
        return false;
    }


    public static void collectBets(boolean[] active, double[] money, double[] bet) {
        output.println("Choix des mises");
        output.println("");
        output.printf("Pour arrêter de jouer, choisir la mise 0, cet arrêt sera définitif.%nSinon, choisir une mise strictement positive.%n");
        output.println("");

        for (int i=0; i<active.length; i++){


            if (active[i]){
                output.printf("Joueur %s, donne ta mise en Euros (entre 0.0 et %s) : ",i+1,money[i]);
                bet[i]= toDouble(input.next());
                while (bet[i]<0 || bet[i] > money[i]) {
                    output.printf("Réponse incorrecte !%n");
                    output.printf("Joueur %s, donne ta mise en Euros (entre 0.0 et %s) : ",i+1,money[i]);
                    bet[i]= toDouble(input.next());

                }





                if (bet[i] == 0) {
                    active[i] = false;
                }

                else{

                    money[i]-=bet[i];
                }

            }
        }
        output.println("\n");
    }

    public static void dealInitialCards(boolean[] playerIsActive, int[][] playerHand, int[] dealerHand, int[] deck) {

        for (int i=0;i<playerIsActive.length;i++){

            if (playerIsActive[i]){

                drawCard(deck,playerHand[i]);

            }


        }

        drawCard(deck,dealerHand);

        for (int i=0;i<playerIsActive.length;i++) {
            if (playerIsActive[i]) {
                drawCard(deck, playerHand[i]);
            }
        }

        drawCard(deck,dealerHand);
    }

    public static double playerNewMoney(double pMoney, double pBet, int pScore,  int dealerScore, boolean pInsur)  {
        if (pScore==-2) {
            output.printf("Vous avez abandonné.") ;
            return pMoney;
        }
        if (pScore==dealerScore) {
            output.println("Tu es à égalité avec le croupier, tu récupères ta mise, soit " + pBet+" €");
            pMoney+=pBet;
        }
        else if (dealerScore==22 || pScore==-1) {
            output.println("Tu perds contre le croupier, tu ne récupères rien.");
        }
        else if (pScore==22) {
	    output.println("Tu gagnes contre le croupier avec un Black Jack, tu récupères 3.0 fois ta mise, soit " + 3*pBet+" €");
            pMoney+=3*pBet;
        }
        else if (pScore<=21 && dealerScore==0) {
            output.println("Tu gagnes contre le croupier, tu récupères 2.5 fois ta mise, soit " + 2.5*pBet+" €");
            pMoney+=2.5*pBet;
        }
        else {
            if (pScore>dealerScore) {
                output.println("Tu gagnes contre le croupier, tu récupères 2.5 fois ta mise, soit " + 2.5*pBet+" €");
                pMoney+=2.5*pBet;
            }
            else {
                output.println("Tu perds contre le croupier, tu ne récupère rien");

            }
        }

        if (pInsur) {
            output.println("Tu t'es assuré contre un Black Jack du croupier.");
            if (dealerScore==22) {
                output.printf("Le croupier a fait un Black Jack, tu récupères 2 fois ta prime d'assurance, soit %s €%n",((pBet/4)*2));
                pMoney+=(pBet/4)*2;
            }
            else {
                output.println("Le croupier n'a pas fait de Black Jack, tu ne récupères rien.");
            }
        }


        return pMoney;

    }

    public static int handvalue(int[] hand, boolean isPlayer) {
        int value = 0;
        boolean hasAnAce = false;
        for (int i = 1; i <= hand[0]; i++) {
            if (hand[i] == 1) {
                hasAnAce = true;
            }
            if (hand[i] == 11 || hand[i] == 12 || hand[i] == 13) {
                value += 10;
            } else {
                value += hand[i];
            }
        }

        if (hasAnAce) {
            if (value <= 11) {
                value += 10;
            }
        }
        return value;
    }

    public static int playDrawingPhase(int[] hand, int minScore,  boolean hasAnAce, int bestScore, boolean isPlayer, int[] deck, boolean  doubleBet) {
        int value=handvalue(hand,isPlayer);
        int drawc;
        if (isPlayer) {
            if (doubleBet) {
                drawc = drawCard(deck, hand);
                hasAnAce = updateHasAnAce(hasAnAce, drawc);
                minScore = updateMinScore(minScore, drawc);
                bestScore = bestScore(minScore, hasAnAce);
                displayNewCardAndPoints(drawc, minScore, bestScore, isPlayer, doubleBet);

                if (handvalue(hand,isPlayer) > 21) {
                    output.printf("Tu as dépassé 21 points !%n%n");
                    return -1;
                }
                output.print("\n");
            } else {
                String rep = "";
                while (handvalue(hand,true) < 21 && !rep.equals("non")) {
                    rep = "";
                    while (!rep.equals("oui") && !rep.equals("non")) {
                        output.print("Veux-tu tirer une carte [oui/non] ? ");
                        rep = input.next();
                    }

                    if (rep.equals("oui")) {
                        drawc = drawCard(deck,hand);
                        hasAnAce = updateHasAnAce(hasAnAce,drawc);
                        minScore = updateMinScore(minScore,drawc);
                        bestScore = bestScore(minScore,hasAnAce);
                        displayNewCardAndPoints(drawc,minScore,bestScore,isPlayer,doubleBet);

                        if (handvalue(hand,isPlayer) > 21) {
                            output.printf("Tu as dépassé 21 points !%n%n");
                            return -1;
                        }
                    }
                }
                output.print("\n");
            }
        }


        else {
            output.printf("Le croupier a les cartes %s et %s.%n",cardName(hand[1]),cardName(hand[2]));
            output.printf("Il a %s points.%n",handvalue(hand,false));
            while (handvalue(hand,isPlayer)<17) {
                drawc=drawCard(deck,hand);
                value=handvalue(hand,false);
                hasAnAce=updateHasAnAce(hasAnAce,drawc);
                minScore=updateMinScore(minScore,drawc);
                bestScore=bestScore(minScore,hasAnAce);
                displayNewCardAndPoints(drawc,minScore,bestScore,isPlayer,doubleBet);


            }
            output.print('\n');
            if (value>21) {
                return 0;
            }
            else if (value==21 && hand[0]==2) {
                return 22;
            }
        }

        return handvalue(hand,isPlayer);

    }

    public static void displayGameInit(boolean[] playerIsActive, double[] playerMoney, double[] playerBet, int[][] playerHand, int dealerVisibleCard) {
        for (int i=0;i<playerIsActive.length;i++){
            if (playerIsActive[i]){
                output.printf("Joueur %s : ",i+1);
                displayPlayerGameState(playerMoney[i],playerBet[i],playerHand[i],false);
                output.print('\n');
            }
        }
        output.print("\n");
        output.printf("Le croupier a les cartes %s et ? .%n",cardName(dealerVisibleCard));
        output.print("\n");
    }

    public static boolean initialHasAnAce( int[] hand) {
        for (int i=1;i<=hand[0];i++) {
            if (hand[i] == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean updateHasAnAce(boolean hasAnAce, int newCard) {
        if (hasAnAce) {
            return true;
        }
        if (newCard==1) {
            return true;
        }
        return false;
    }



    public static int initialMinScore(int[] hand) {
        if (initialHasAnAce( hand)) {
            return handvalue(hand,true)-10;
        }
        return handvalue(hand,true);
    }

    public static int updateMinScore(int minScore,int newCard) {
        if (newCard >= 10) {
            return minScore+10;
        }
        return minScore+newCard;

    }

    public static int initialBestScore(int[] hand) {
        return handvalue(hand,true);
    }

    public static int bestScore(int minScore,boolean hasAnAce) {
        if (minScore<=11 && hasAnAce) {
            return minScore+10;
        }
        return minScore;
    }

    public static boolean chooseDoubleBet(int i, double[] money, double[] bet, int[] pHand) {


        if (bet[i] > money[i]) {
            output.printf("Tu n'as plus assez d'argent pour doubler ta mise.%n");
            return false;
        }

        output.printf("Veux-tu doubler ta mise (et tirer une unique carte) [oui/non] ? ");
        String rep = input.next().trim().toLowerCase();

        while (!rep.equals("oui") && !rep.equals("non")) {
            output.printf("Réponse incorrecte%n");
            output.printf("Veux-tu doubler ta mise (et tirer une unique carte) [oui/non] ? ");
            rep = input.next().trim().toLowerCase();
        }

        if (rep.equals("oui")) {
            money[i] -= bet[i];
            bet[i] *= 2;

            output.println("Vous avez doublé votre mise !");
            output.println("Nouvelle mise : " + bet[i]);
            output.println("Nouveau solde : " + money[i]);
            output.println("Rappel : Vous ne tirerez plus qu'une seule carte supplémentaire. Et oui, c'est la règle !!!");

            return true;
        }

        return false;
    }


    public static boolean chooseInsurance(int i, double[] money, double pBet, int[] pHand)  {

    if(money[i]<(pBet/4)) {
        output.println("Solde insuffisant pour obtenir une assurance !");
        return false;
    }
    output.printf("Veux-tu t'assurer pour le quart de ta mise, soit %s € contre un Black Jack du croupier [oui/non] ?",pBet/4);
    String rep = input.next();

    while (!rep.equals("oui") && !rep.equals("non")) {
        output.printf("Réponse incorrecte%n");
        output.printf("Veux-tu t'assurer pour le quart de ta mise, soit %s € contre un Black Jack du croupier [oui/non] ?",pBet/4);
        rep = input.next();
    }

    if (rep.equals("oui")) {
        money[i] -= pBet/4;
        displayPlayerGameState(money[i],pBet,pHand,true);
        return true;
    }
    return false;
}

public static boolean chooseSurrender (double pBet)  {
    //Résultat : retourne true si le joueur décide d’abandonner et de récupérer la moitié de sa mise, et false sinon.
    output.printf("Veux-tu abandonner ce tour et récupérer la moitié de ta mise, soit %s € [oui/non] ?%n",pBet/2);
    String rep = input.next();
    while (!rep.equals("oui") && !rep.equals("non")) {
        output.printf("Réponse incorrecte%n");
        output.printf("Veux-tu abandonner ce tour et récupérer la moitié de ta mise, soit %s € [oui/non] ?%n",pBet/2);
        rep = input.next();
    }
    if (rep.equals("oui")) {
        return true;
    }

    return false;
}


    public static int playerPlayTurn(int i, double[] money, double[] bet,
                                     int [] pHand, int[] deck,
                                     boolean[] insurance, boolean dealerShowsAce) {
        output.printf("--> Tour du joueur %s%n", i+1);
        displayPlayerGameState(money[i],bet[i],pHand,insurance[i]);

        if (handvalue(pHand,true) == 21 && pHand[0]==2) {
            output.printf("Black Jack !!!%n%n");
            return 22;
        }

        int minScore = initialMinScore(pHand);
        int bestScore = initialBestScore(pHand);
        boolean hasAnAce = initialHasAnAce(pHand);

        output.printf("Tu as %s points.%n", handvalue(pHand,true));

        boolean doubleBet = chooseDoubleBet(i, money, bet, pHand);

        if (doubleBet) {
            return playDrawingPhase(pHand, minScore, hasAnAce, bestScore, true, deck, true);
        }

        if (dealerShowsAce && !insurance[i]) {
            insurance[i] = chooseInsurance(i, money, bet[i], pHand);
        }

        if (chooseSurrender(bet[i])) {
            money[i] += bet[i]/2;
            output.printf("Vous avez abandonné. Vous récupérez la moitié de votre mise, soit %s €.%n%n", bet[i]/2);
            return -2;
        }

        return playDrawingPhase(pHand, minScore, hasAnAce, bestScore, true, deck, false);
    }


    public static int dealerPlayTurn(int[] hand, int[] deck) {
        output.printf("--> Tour du croupier%n");
        int dealerScore = playDrawingPhase(hand, initialMinScore(hand), initialHasAnAce(hand), initialBestScore(hand), false, deck,false);
        if (dealerScore == 0) {
            output.printf("Le croupier a dépassé 21 !%n%n");
            return 0;
        }
        else if (dealerScore == 21 && hand[0]==2) {

            return 22;
        }
	    return dealerScore;

    }

    public static int playTurn(boolean[] playerIsActive, double[] money, double[] bet,
                               int[][] playerHand, int [] dealerHand, int[] PlayerScore,
                               int[] deck, boolean[] insurance) {
        output.println("Faites vos jeux !");
        output.print('\n');

        // Le croupier montre-t-il un As ?
        boolean dealerShowsAce = (dealerHand[1] == 1);

        for (int i = 0; i < playerIsActive.length; i++) {
            if (playerIsActive[i]) {
                PlayerScore[i] = playerPlayTurn(i, money, bet, playerHand[i],
                        deck, insurance, dealerShowsAce);
            } else {
                PlayerScore[i] = -1;
            }
        }
        return dealerPlayTurn(dealerHand, deck);
    }


    public static void displayPlayerGameState(double money, double bet, int[] hand, boolean pInsur) {
        if (pInsur) {
            output.printf("solde = %s € / mise = %s € / assurance = %s € / cartes : ", money, bet, (bet/4));

            if (hand[0] > 2) {
                for (int i = 1; i < hand[0] - 1; i++) {
                    output.printf(cardName(hand[i]) + ", ");
                }
            }


            output.printf("%s et %s%n", cardName(hand[hand[0] - 1]), cardName(hand[hand[0]]));

        }

        else {
            output.printf("solde = %s € / mise = %s € / cartes : ", money, bet);

            if (hand[0] > 2) {
                for (int i = 1; i < hand[0] - 1; i++) {
                    output.printf(cardName(hand[i]) + ", ");
                }
            }


            output.printf("%s et %s%n", cardName(hand[hand[0] - 1]), cardName(hand[hand[0]]));

        }
    }

    public static void displayNewCardAndPoints(int newCard, int minScore,  int bestScore, boolean isPlayer, boolean doubleBet) {
        if (isPlayer) {
            if (minScore != bestScore) {
                if (doubleBet) {
                    output.printf("Tu as tiré un %s. Tu as %s points.%n",cardName(newCard),bestScore);
                }
                else {
                    output.printf("Tu as tiré un %s. Tu as %s ou %s points.%n",cardName(newCard),minScore,bestScore);
                }
            }
            else {
                output.printf("Tu as tiré un %s. Tu as %s points.%n",cardName(newCard),bestScore);
            }

        }
        else {
            output.printf("Le croupier a tiré un %s. Il a %s points.%n",cardName(newCard), bestScore);
        }
    }

    public static void displayPlayerResult(int i, double pMoney, double pBet, int [] pHand, int pScore, int dealerScore, double pNewMoney, boolean pInsur)  {
        output.printf("Résultat du joueur %s%n", i+1);
        displayPlayerGameState(pMoney,pBet,pHand,pInsur);
        output.printf("Tu as %s points.%n",handvalue(pHand,true));
        playerNewMoney(pMoney,pBet,pScore,dealerScore,pInsur);
        output.printf("Ton solde est de %s €%n",pMoney);
        output.print('\n');

    }



    public static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -99;
        }
    }

    public static double toDouble(String s) {
        try {

            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return -67;
        }
    }


    public static void main(String[] args) {
        int nbjoueur=0;
        output.print("Donner le nombre de joueurs (entre 1 et 6) : ");
        nbjoueur = toInt(input.next());
        while ( nbjoueur<1 || nbjoueur>6) {
            output.print("Réponse incorrecte ! ");
            nbjoueur = toInt(input.next());
        }
        output.print("\n");


        int nbPacks=0;

        while (nbPacks<1 || nbPacks>8) {
            output.print("Donner le nombre de paquets de 52 cartes utilisés (entre 1 et 8) : ");
            nbPacks = toInt(input.next());
        }
        output.print("\n");
        playGame(nbjoueur,nbPacks);
    }


    public static void processAndDisplayResults(boolean[] playerIsActive, double[] money,
                                                double[] bet, int [][] playerHand,
                                                int[] playerScore, int dealerScore,
                                                boolean[] insurance) {
        for (int i=0;i<playerIsActive.length;i++) {
            output.printf("Résultat du joueur %s%n", i+1);
            displayPlayerGameState(money[i],bet[i],playerHand[i],insurance[i]);

            if (playerScore[i] == -1) {
                output.printf("Tu as dépassé 21 points.%n");
            } else {
                output.printf("Tu as %s points.%n",handvalue(playerHand[i],true));
            }

            money[i] = playerNewMoney(money[i],bet[i],playerScore[i],dealerScore,insurance[i]);
            output.printf("Ton solde est de %s €%n",money[i]);
            output.print('\n');
        }
    }



}
