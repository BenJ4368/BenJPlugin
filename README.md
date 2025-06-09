# BriochePlugin

Un plugin Minecraft 1.20.4 sous PaperMC développé par **BenJ4368** pour la communauté du serveurdedart.fr ([Discord](discord.gg/dart
), [Dart](https://www.youtube.com/@darteuh)).

## 🧠 Modules

- `chatMention` : Gestion des mentions et du son joué.
- `chestLocker` : Gestion du verrouillage des coffres et conteneurs.

### 💬 ChatMention

  Averti le joueur avec un son lorsque son pseudo est mentionné dans le chat.<br>
  Aucun son n'est joué si le joueur se mentionne lui-même.<br>

### 🔒 ChestLocker
  Permet le verrouillage, déverrouillage, ajout et retrait de droits sur conteneurs via des clés.<br>
  `/chestlock <lock | unlock | add | remove | check>`<br>

  Les clés 🔑 ne sont utilsable dans aucunes recettes de fabrication.<br>
  Les clés 🔑 ne sont pas droppable, et les Piglins 🐽 ne les échangent pas<br>
  Un conteneur verrouillé ne peux être détruis ni par un joueur, ni par une explosion 💥.<br>
  Aucun bloc n'est cassable si le joueur tiens une clé 🔑, pour éviter la destruction des conteneurs en créatif, et des DecoratedPot en survie.<br>

  **Les conteneurs verrouillable sont:**<br>
  
  ![Chest, trapped chest, barrel, furnace, smoker, blast furnace, lectern, chiseled bookshelf, decorated pot, shulkerbox, hopper, dropper, dispenser, brewing stand](images/container_list.png)

  **Les interactions comprenant au moins un conteneur verrouillé sont limitées. En voici le details:**<br>
  *Bloc d'or = conteneur verrouillé*<br>
  *flèche verte = interaction autorisée*<br>
  *flèche rouge = interaction stoppée*<br>

  ![Un hopper verrouillé ne peux pas aspirer d'items depuis le monde](images/showInteraction0.png)

  ![Un hopper non verrouillé peux verser dans, mais pas aspirer depuis un conteneur verrouillé.](images/showInteraction1.png)
  ![Un hopper verrouillé peux verser dans et aspirer depuis un conteneur verrouillé](images/showInteraction2.png)

  ![Un hopper non verrouillé ne peux pas aspirer depuis un hopper verrouillé, même si le second verse dans le premier](images/showInteraction3.png)
  ![Pour palier à ce soucis, placez simplement un conteneurs non verrouillés entre les deux](images/showInteraction4.png)


## 🧑‍💻 Développement

- Plugin principal : `me.benj.brioche.BriochePlugin`
- Version du plugin: `1.0-SNAPSHOT`
- Build tool : **Maven**
- Java 17+

## 🔗 Auteurs

- [BenJ4368](https://github.com/BenJ4368) — Développement initial
