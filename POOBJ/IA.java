
/**
 * La classe IA définit le comportement intelligent du joueur.
 * Elle repose sur le calcul de l'espérance de gain (gain attendu) en comparant 
 * deux stratégies : "Stand" (s'arrêter) vs "Draw" (tirer une carte).
 */
public class IA {

    private int dealerCardMinValue;

    // Matrices d'espérance de gain pré-calculées
    // [ScoreJoueur][CarteCroupier]
    private static double[][] gainExpectedIfStands = new double[22][10];
    // [ScoreMinJoueur][PossèdeAs][CarteCroupier]
    private static double[][][] gainExpectedIfDraws = new double[21][2][10];

    /**
     * Constructeur de l'IA pour un tour donné.
     * @param aCardMinValue Valeur faciale de la carte visible du croupier (1 à 10).
     */
    public IA(int aCardMinValue) {
        this.dealerCardMinValue = aCardMinValue - 1;
    }

    // --- Logique de Simulation (Monte-Carlo) ---

    /**
     * Simule un tour complet du croupier à partir d'une carte initiale.
     * @return Le résultat codé : 0 si Bust (>21), 1 à 5 pour les scores 17 à 21, 6 pour Blackjack.
     */
    public static int simulation(int i) {
        Hand h = new Hand(21);
        
        int rank = i + 1;
        h.addCard(new Card(rank));
    
        while (h.bestScore() < 17 && h.bestScore() > 0) {
            
            int randomRank = 1 + CardSequence.random.nextInt(13);
            h.addCard(new Card(randomRank));
        }

        int score = h.bestScore();

        if (score == 0) return 0;       // Le croupier a sauté (Bust)
        if (score == 22) return 6;      // Blackjack (si votre classe Hand le gère)
        

        
        return score - 16;
        


    }

    /**
     * Calcule les probabilités des scores finaux du croupier pour une carte donnée.
     */
    public static double[] computeLineDealerSP(int i, int nbSimul) {
        double[] proba = new double[7];
        for (int s = 0; s < nbSimul; s++) {
            int result = simulation(i);
            proba[result] += 1.0;
            
        }
        for (int r = 0; r < proba.length; r++) {
            proba[r] /= nbSimul;
        }

        return proba;
    }

    /**
     * Remplit la matrice globale des probabilités du score du croupier.
     */
    public static double[][] computeDealerScoreProba(int nbSimul) {
        double[][] probaMatrix = new double[10][7];
        for (int i = 0; i < 10; i++) {
            probaMatrix[i] = computeLineDealerSP(i, nbSimul);
        }
        return probaMatrix;    
    }

    /**
     * Vérifie si deux matrices de probabilités sont proches à un epsilon près.
     */
    public static boolean checkSameProba(double[][] m1, double[][] m2, double epsilon) {
        for (int i=0; i<m1.length; i++) {
            for (int j=0; j<m1[i].length; j++) {
                if (Math.abs(m1[i][j] - m2[i][j]) > epsilon) {
                    return false;
                }
            }
        }
        return true;
	
    }   

    /**
     * Détermine le nombre de simulations nécessaires pour stabiliser les probabilités.
     */
    public static double[][] computeDealerScoreProba(double epsilon) {
        double[][] previousProba = new double[10][7];
        double[][] currentProba = new double[10][7];
        int nbSimul = 1000;
        do {
            previousProba = currentProba;
            currentProba = computeDealerScoreProba(nbSimul);
            nbSimul *= 2;
        } while (!checkSameProba(previousProba, currentProba, epsilon));
        return currentProba;
    }

    // --- Calcul des Espérances ---

    /**
     * Pre-requis : 0 <= bestScore <= 21 et 0 <= y <= 6
     * Resultat : le gain du joueur (-1, 0 ou 1.5) si son
     *   score final est bestScore et celui du croupier est
     *   represente par y
     */
    public static double gain(int bestScore, int y) {
        if (y == 0) {
            if (bestScore > 0) {
                return 1.5; 
            } 
            else{
                return -1.0; 
            } 
        }
        else if (y == 6) {
                return -1.0; 
        }
        else {
            int dealerScore = y + 16;
            if (bestScore > dealerScore) {
                return 1.5; 
            } 
            else if (bestScore == dealerScore) {
                return 0.0; 
            } 
            else{
                return -1.0; 
            } 
        }
    }

    /**
     * Remplit la matrice de gain si le joueur décide de s'arrêter (Stand).
     */
    public static void computeGainExpectedIfStands() {

    
        double [][] dealerScoreProba = computeDealerScoreProba(0.1);
        
        for ( int plays =0; plays<=21;plays++) {
            for (int dc =0; dc<10;dc++) {
                double sum=0.0;
                for (int dealers=0; dealers<7; dealers++) {
                    sum += gain(plays, dealers) * dealerScoreProba[dc][dealers];
                }
                
                gainExpectedIfStands[plays][dc] = sum;
            }
        }

    }

    /**
     * Calcule le meilleur score théorique après avoir pioché une carte spécifique.
     */
    public static int theBestScore(int minScore, int hasAnAce, int rank) {
      
        int cardVal = rank;
        if (rank >= 10) {
            cardVal = 10;
        }
        
        int newMin = minScore + cardVal;

        if (newMin > 21) {
            return 0; 
        }

        boolean activeAce = (hasAnAce == 1) || (rank == 1);

        if (activeAce && newMin <= 11) {
            return newMin + 10;
        } else {
            return newMin;
        }
    }

    /**
     * Calcule l'espérance de gain moyenne si le joueur tire une carte supplémentaire.
     */
    public static void computeGainExpectedIfDraws() {
        
        
        
        computeGainExpectedIfStands();

        for (int min = 1; min <= 20; min++) { 
            for (int hasAnAce = 0; hasAnAce <= 1; hasAnAce++) {
                for (int dealerC = 0; dealerC < 10; dealerC++) {
                    double sum = 0;
                    for (int rank = 1; rank <= 13; rank++) {
                        int nextScore = theBestScore(min, hasAnAce, rank);
                        
                        if (nextScore == 0) {
                            sum += -1.0;
                        } else {
                            sum += gainExpectedIfStands[nextScore][dealerC];
                        }
                    }
                    
                    gainExpectedIfDraws[min][hasAnAce][dealerC] = sum / 13.0;
                }
            }
        }
    }

    // --- Méthodes de décision (Utilisées par Player) ---
    
    /**
     * Décision : Tirer si l'espérance de gain en tirant est supérieure à celle de s'arrêter.
     */
    public boolean chooseToDraw(int minScore, boolean hasAnAce, int bestScore) {
        int aceIndex = 0;
        if (bestScore >= 21) return false;
        if (minScore == 0) return false;

        if (hasAnAce) {
           aceIndex = 1;
        }
        return gainExpectedIfDraws[minScore][aceIndex][dealerCardMinValue] > gainExpectedIfStands[bestScore][dealerCardMinValue];
    }

    /** * Décision : Doubler si on a l'intention de tirer ET que l'espérance est positive.
     */
    public boolean chooseDoubleBet(int minScore, boolean hasAnAce, int bestScore) {
        int aceIndex = 0;
        if (hasAnAce) {
           aceIndex = 1;
        }
        double draw = gainExpectedIfDraws[minScore][aceIndex][dealerCardMinValue];
        double stand = gainExpectedIfStands[bestScore][dealerCardMinValue];
        
        return (draw > 0) && (2 * draw > stand);
    }

    /** * Décision : Abandonner si s'arrêter est préférable à tirer, mais que l'espérance reste négative.
     */
    public boolean chooseToSurrender(int minScore, boolean hasAnAce, int bestScore) {
        int aceIndex = 0;
        if (hasAnAce) {
           aceIndex = 1;
        }
        double draw = gainExpectedIfDraws[minScore][aceIndex][dealerCardMinValue];
        double stand = gainExpectedIfStands[bestScore][dealerCardMinValue];
        
        
        return Math.max(draw, stand) < -0.5;
    }

    /**
     * Justification mathématique de l'assurance :
     * L'assurance coûte 1/4 de la mise et rapporte 2x si le croupier a un Blackjack.
     * La probabilité que le croupier ait un 10 (10, J, Q, K) est de 4/13.
     * Espérance = (4/13 * Gain) + (9/13 * Perte) = (4/13 * 0.5) + (9/13 * -0.25) = 2/13 - 2.25/13 = -0.25/13.
     * L'espérance est négative, donc l'IA retourne 'false'.
     */
    public boolean chooseInsurance() {
        return false;
    }

} // end class IA

