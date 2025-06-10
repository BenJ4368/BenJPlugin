# BriochePlugin

Un plugin Minecraft 1.20.4 sous PaperMC développé par **BenJ4368**<br>
pour la communauté du serveurdedart.fr ([Discord](discord.gg/dart), [Dart](https://www.youtube.com/@darteuh)).

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

  ![Source peux toujours donner. Destination ne peux prendre que si la source n'est pas verrouillée, ou si Destionation est Source sont verrouillées par le même joueur.](images/showInteractions.png)


## 🧑‍💻 Développement

- Plugin principal : `me.benj.brioche.BriochePlugin`
- Version du plugin: `1.0-SNAPSHOT`
- Build tool : **Maven**
- Java 17+

## 🔗 Auteurs

- [BenJ4368](https://github.com/BenJ4368) — Développement initial
