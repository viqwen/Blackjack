# Blackjack

Implémentation du jeu du Blackjack en Java, avec une stratégie de joueur
automatique fondée sur le calcul d'espérance de gain.

Auteur : Viqwen

## Fonctionnement

Chaque tour melange un sabot composé de 1 à 8 paquets de 52 cartes, puis distribue deux cartes à chaque joueur et au croupier. Chaque joueur choisit sa mise, puis décide en connaissance de cause de tirer, de s'arrêter, de doubler sa mise, de prendre une assurance ou d'abandonner ; le croupier tire automatiquement jusqu'à atteindre au moins 17 points. Le score de chaque main est calculé à partir du meilleur total inférieur ou égal à 21 possible, un As valant 1 ou 11, et les gains sont réglés en fonction de la confrontation entre le joueur et le croupier. Un mode simulation permet d'évaluer statistiquement la stratégie automatique sur des milliers de parties.

## Stack technique

- Java (aucune dépendance externe, JDK 11+)
- Collections et I/O standard : `java.util`, `java.io`
- Génération aléatoire : `java.util.Random` (mélange par permutation de Fisher-Yates)
- Simulation Monte-Carlo pour le calcul des probabilités du croupier
- Outillage : Checkstyle (configuration `blackjack-style.xml`)

## Compilation et exécution

Depuis la racine du projet :

```bash
javac -d out POOBJ/*.java
java -cp out BlackjackMain
```

## Organisation du projet

| Fichier | Rôle |
| --- | --- |
| `POOBJ/BlackjackMain.java` | Point d'entrée : menu interactif et mode simulation |
| `POOBJ/BlackjackGame.java` | Orchestration d'une session (mises, distribution, tours, résultats) |
| `POOBJ/Player.java` | Joueur humain ou piloté par l'IA : solde, mise, main, décisions |
| `POOBJ/Dealer.java` | Croupier et sa règle de pioche automatique |
| `POOBJ/IA.java` | Stratégie : calcul des espérances de gain (tirer / s'arrêter / doubler / abandonner) |
| `POOBJ/Hand.java` | Main de cartes et calcul du score minimal et optimal |
| `POOBJ/Card.java`, `POOBJ/CardSequence.java`, `POOBJ/Shoe.java` | Modèle de carte, séquence de cartes et sabot |
| `POOBJ/UserInterface.java` | Affichage console et saisie des décisions |
| `Blackjack.java`, `ExtendedBlackjack.java` | Versions procédurales du jeu, sans POO |
| `ExtractMethodText.java` | Utilitaire d'extraction de source |
