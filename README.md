# BenJ Plugin

Plugins Minecraft 1.20.4 sous PaperMC développés par **BenJ4368**<br>
pour la communauté du serveurdedart.fr ([Dart](https://www.youtube.com/@darteuh), [Discord](discord.gg/dart)).

## Liste des plugins

- `ChatMentioner` : Joue un son à un joueur quand mentionné.
- [en cours] `ChunkClaimer` : Permet la protection du terrain.
- `ChestLocker` : Permet le verrouillage des conteneurs.

### 💬 ChatMentioner

  Averti le joueur avec un son lorsque son pseudo est mentionné dans le chat.<br>
  ChatMentioner est activé par défaut, mais peux être desactiver :<br>
  `/chatmentionner <on | off>`<br>
  Une mention qui proviens d'un modérateur outrepasse ce paramètre, et le son en est différent.<br>

### [en cours] 🛡️ ChunkClaimer

  Permet la protection du terrain et previens les interactions non autorisés.
  `/chunkclaimer <claim | unclaim | add | remove | check>`


### 🔒 ChestLocker
  Permet le verrouillage, déverrouillage, ajout et retrait de droits sur conteneurs via des clés.<br>
  `/chestlocker <lock | unlock | add | remove | check>`<br>

  Les clés 🔑 ne sont utilsable dans aucunes recettes de fabrication.<br>
  Les clés 🔑 ne sont pas droppable, et les Piglins 🐽 ne les échangent pas<br>
  Un conteneur verrouillé ne peux être détruis ni par un joueur, ni par une explosion 💥.<br>
  Un itemframe posé sur un conteneur verrouillé est également verrouillé.
  Aucun bloc n'est cassable si le joueur tiens une clé 🔑, pour éviter la destruction des conteneurs en créatif, et des DecoratedPot en survie.<br>

  **Les conteneurs verrouillable sont:**<br>

  ![Chest, trapped chest, barrel, furnace, smoker, blast furnace, lectern, chiseled bookshelf, decorated pot, shulkerbox, hopper, dropper, dispenser, brewing stand](images/container_list.png)

  **Les interactions comprenant au moins un conteneur verrouillé sont limitées. En voici le details:**<br>

  ![Source peux toujours donner. Destination ne peux prendre que si la source n'est pas verrouillée, ou si Destionation est Source sont verrouillées par le même joueur.](images/showInteractions.png)


## 🧑‍💻 Développement

- Build tool : **Maven**
- Java 17+
- [BenJ4368](https://github.com/BenJ4368) — Développement principale
