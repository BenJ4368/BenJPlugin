# BriochePlugin

Un plugin Minecraft Paper 1.20.4 développé par **BenJ4368** pour la communauté du serveurdedart.fr ([Discord](discord.gg/dart
), [Dart](https://www.youtube.com/@darteuh)).

## 🧠 Modules

- `chatMention` : Gestion des mentions et du son joué.
- `chestLocker` : Gestion du verrouillage des coffres et conteneurs.

## ✨ Fonctionnalités actuelles

- **chatMention**:<br>
	Averti le joueur avec un son lorsque son pseudo est mentionné dans le chat.<br>
	Aucun son n'est joué si le joueur se mentionne lui-même.

- **(En cours) chestLocker**:<br>
  Permet le verrouillage, déverrouillage, ajout et retrait de droits sur conteneurs via des clés.<br>
  `/chestlock <lock | unlock | add | remove>`<br>

  Un conteneur verrouillé ne peux être détruis ni par un joueur, ni par une explosion.<br>

  Les "conteneurs" verrouillable sont:<br>
  ![Chest, trapped chest, barrel, furnace, smoker, blast furnace, lectern, chiseled bookshelf, decorated pot, shulkerbox, hopper, dropper, dispenser, brewing stand](readme_containerlist_resized.jpg)


## 🧑‍💻 Développement

- Plugin principal : `me.benj.brioche.BriochePlugin`
- Version du plugin: `1.0-SNAPSHOT`
- Build tool : **Maven**
- Java 17+

## 🔗 Auteurs

- [BenJ4368](https://github.com/BenJ4368) — Développement initial
