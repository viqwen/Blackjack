import java.util.Random;
import java.util.Scanner;
import java.util.Locale;
import java.io.PrintStream;

public class Blackjack {

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
        collectBets(active,money,bet);

        if (!playersRemain(active)) {
            return false;
        }
        dealInitialCards(active, playerHand, dealerHand, deck);

        displayGameInit(active,money,bet,playerHand,dealerHand[1]);
        int DealerScore = playTurn(active,money,bet,playerHand,dealerHand,PlayerScore,deck);

        output.printf("--> Résultats de la partie <--%n%n");

        output.printf("Le croupier a %s points.%n%n",handvalue(dealerHand,false));

        for (int i=0;i<active.length;i++) {
            if (active[i]) {
                output.printf("Résultat du joueur %s%n", i + 1);
                afficheinfo(money[i], bet[i], playerHand[i]);
                output.printf("Tu as %s points.%n",handvalue(playerHand[i],true));
                money[i]=playerNewMoney(money[i], bet[i], PlayerScore[i], DealerScore);
                output.printf("%n%n");
            }
        }

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

    public static double playerNewMoney(double pMoney, double pBet, int pScore, int dealerScore) {
        if (pScore==22 && dealerScore==22){
	    output.print("Tu es à égalité avec le croupier, tu récupères ta mise, soit " + pBet+" €" + "\nTon solde est de " + (pMoney+pBet) +" €");
            return pMoney+pBet;
        }
        else if (dealerScore==22 || pScore==-1) {
            output.print("Tu perds contre le croupier, tu ne récupères rien." + "\nTon solde est de " + pMoney+" €");
            return pMoney;
        }
        else if (pScore==22) {
	    output.printf("Tu gagnes contre le croupier avec un Black Jack, tu récupères 3.0 fois ta mise, soit " + 3*pBet+" €" + "\nTon solde est de " + (pMoney+3*pBet)+" €");
            return pMoney+3*pBet;
        }
        else if (pScore<=21 && dealerScore==0) {
            output.print("Tu gagnes contre le croupier, tu récupères 2.5 fois ta mise, soit " + 2.5*pBet+" €" + "\nTon solde est de " + (pMoney+2.5*pBet)+" €");
            return pMoney+2.5*pBet;
        }
        else {
            if (pScore==dealerScore) {
		output.print("Tu es à égalité avec le croupier, tu récupères ta mise, soit " + pBet+" €" +  "\nTon solde est de " + (pMoney+pBet)+" €");
                return pMoney+pBet;
            }
            else if (pScore>dealerScore) {
                output.print("Tu gagnes contre le croupier, tu récupères 2.5 fois ta mise, soit " + 2.5*pBet+" €" + "\nTon solde est de " + (pMoney+2.5*pBet)+" €");
                return pMoney+2.5*pBet;
            }
            else {
                output.print("Tu perds contre le croupier, tu ne récupère rien" + " \nTon solde est de " + pMoney +" €");
                return pMoney;
            }
        }

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

    public static int playDrawingPhase(int[] hand, int minScore, boolean hasAnAce, int bestScore, boolean isPlayer, int[] deck) {
        int value=handvalue(hand,isPlayer);
        int drawc;
        if (isPlayer) {
            if (hasAnAce) {
                output.printf("Tu as %s ou %s points.%n",minScore,bestScore);
            }
            else {
                output.printf("Tu as %s points.%n",bestScore);
            }


            String rep="";
            while (handvalue(hand,true)<21&&!rep.equals("non")){
                rep="";
                while (!rep.equals("oui")&&!rep.equals("non")) {
                    output.print("Veux-tu tirer une carte [oui/non] ? ");
                    rep = input.next();

                }

                if (rep.equals("oui")) {
                    drawc=drawCard(deck,hand);
                    output.printf("Tu as tiré un %s. Tu as %s points.%n",cardName(drawc),handvalue(hand,true));
                    if (handvalue(hand,isPlayer) > 21) {
                        output.printf("Tu as dépassé 21 points !%n%n");
                        return -1;
                    }
                    
                }

            }
            output.print("\n");

        }

        else {
            output.printf("Le croupier a les cartes %s et %s.%n",cardName(hand[1]),cardName(hand[2]));
            output.printf("Il a %s points.%n",handvalue(hand,false));
            while (handvalue(hand,isPlayer)<17) {
                drawc=drawCard(deck,hand);
                value=handvalue(hand,false);
                output.printf("Le croupier a tiré un %s. Il a %s points.%n",drawc, value);
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
                afficheinfo(playerMoney[i],playerBet[i],playerHand[i]);
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

    public static int playerPlayTurn(int i, double pMoney, double pBet, int[] pHand, int[] deck) {
        output.printf("--> Tour du joueur %s%n", i+1);
        afficheinfo(pMoney,pBet,pHand);
        int minScore = initialMinScore(pHand);
        int bestScore = initialBestScore(pHand);
        boolean hasAnAce = initialHasAnAce( pHand);
      
        if (handvalue(pHand,true) == 21 && pHand[0]==2) {
            output.printf("Black Jack !!!%n%n");
            return 22;
        }
        return playDrawingPhase(pHand, minScore, hasAnAce, bestScore, true, deck);
        
    }

    public static int dealerPlayTurn(int[] hand, int[] deck) {
        output.printf("--> Tour du croupier%n");
        int dealerScore = playDrawingPhase(hand, 0, false, 0, false, deck);
        if (dealerScore == 0) {
            output.printf("Le croupier a dépassé 21 !%n%n");
            return 0;
        }
        else if (dealerScore == 21 && hand[0]==2) {

            return 22;
        }
	return dealerScore;
    }

    public static int playTurn(boolean[] playerIsActive, double[] money, double[] bet, int[][] playerHand, int [] dealerHand, int[] PlayerScore, int[] deck) {
        output.println("Faites vos jeux !");
        output.print('\n');
        for (int i = 0; i < playerIsActive.length; i++) {
            if (playerIsActive[i]) {
                PlayerScore[i] = playerPlayTurn(i, money[i], bet[i], playerHand[i], deck);
            } else {
                PlayerScore[i] = -1;
            }
        }
	return dealerPlayTurn(dealerHand, deck);
    }

    public static void afficheinfo(double money, double bet, int[] hand) {
        output.printf("solde = %s € / mise = %s € / cartes : ", money, bet);

        if (hand[0]>2) {
            for (int i=1; i<hand[0]-1;i++ ) {
                output.printf(cardName(hand[i])+", ");
            }
        }


        output.printf("%s et %s%n",cardName(hand[hand[0]-1]),cardName(hand[hand[0]]));


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
}
