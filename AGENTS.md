# AGENTS.md

# 1. Instructions générales

Ce fichier contient les informations et règles nécessaires aux agents travaillant sur ce projet.

Il sert à :

- expliquer le but du projet ;
- décrire son architecture ;
- documenter ce qui existe déjà ;
- définir les conventions du projet ;
- coordonner le travail de plusieurs agents ;
- conserver les décisions architecturales importantes ;
- éviter la duplication de fonctionnalités ;
- permettre à un nouvel agent de comprendre rapidement l'état du projet.

Les agents doivent lire ce fichier avant toute modification importante du projet.

---

# 2. Langue

Toutes les communications avec l'utilisateur doivent être réalisées en français.

Sauf indication contraire :

- les noms de classes doivent être en anglais ;
- les noms de méthodes doivent être en anglais ;
- les noms de variables doivent être en anglais ;
- les noms de packages doivent être en anglais ;
- les commentaires dans le code doivent être en anglais ;
- la documentation technique interne au code doit être en anglais.

La documentation destinée à l'utilisateur peut être en français.

---

# 3. Présentation du projet

## Nom du projet

Nomos Engine.

## Description

Nomos Engine est une bibliothèque Java générique de la suite Olympus. Elle fournit un moteur extensible capable de transformer une phrase composée de tokens enregistrés en une action magique structurée, de calculer son coût, de valider ses conditions puis de consommer atomiquement les ressources nécessaires.

La bibliothèque ne dépend d'aucun framework applicatif ou de jeu. Elle peut être utilisée directement ou intégrée dans n'importe quel environnement Java 25. Les applications définissent leur vocabulaire en créant des classes annotées qui étendent les abstractions publiques de Nomos.

Le projet transpose le comportement fonctionnel de MagicToken dans l'architecture commune d'Olympus : façade singleton, stockage central, enregistrement manuel et découverte automatique par annotation. Les tokens concrets historiques sont conservés dans le module `Exemple` et ne font pas partie du moteur principal.

## Objectifs principaux

- Fournir un langage magique extensible basé sur des tokens.
- Respecter le cycle d'initialisation et d'enregistrement des bibliothèques Olympus.
- Rester indépendant de tout framework et garantir un usage concurrent sûr.

## Fonctionnalités prévues

- Enregistrement manuel et automatique de définitions annotées.
- Compilation sémantique, propriétés d'action et expressions numériques.
- Calcul des coûts, validation des conditions et paiement atomique des ressources.
- Module d'exemple contenant le vocabulaire historique de MagicToken.
- Production locale d'un bundle signé destiné à une publication manuelle.

---

# 4. Technologies utilisées

## Langages

- Java 25.

## Build

- Gradle 9.2.0 avec Gradle Wrapper.
- Plugins `java-library`, `maven-publish` et `signing` pour le module principal.
- Plugin `application` pour le module `Exemple`.

## Frameworks

- Aucun framework obligatoire.

## Bibliothèques principales

- ClassGraph 4.8.184 pour la découverte des classes annotées.
- JUnit Jupiter 5.10.0 pour les tests.

## Environnement

- IDE principal : IntelliJ IDEA.
- Système cible : toute JVM Java 25.

---

# 5. Architecture générale

```text
Nomos-Engine
├── src/main/java/fr/olympus/nomos
│   ├── Nomos.java             façade et cycle de vie Olympus
│   ├── annotation             contrat d'annotation des tokens
│   ├── register               scan ClassGraph et types d'enregistrement
│   ├── resources              stockage central NomosData
│   ├── language               abstractions, tokenizer et registre
│   ├── math                   expressions numériques
│   ├── semantic               significations, vecteurs et stratégies d'ordre
│   ├── action                 contributions et compilation
│   ├── condition              validation des sorts
│   ├── cost                   coûts et ressources
│   └── cast                   lancement des sorts
├── src/test                   tests unitaires et de concurrence
└── Exemple
    └── vocabulaire et application de démonstration
```

## Modules

### Module : Nomos-Engine

Responsabilité :

Fournir l'API et l'implémentation générique du moteur de magie.

Contient notamment :

- la façade `Nomos` et `NomosData` ;
- le registre et la découverte des tokens ;
- le compilateur, les expressions, les actions et la sémantique ;
- les conditions, coûts, ressources et services de lancement.

Dépendances :

- ClassGraph 4.8.184.

Ne doit pas :

- dépendre d'un framework applicatif ;
- contenir les tokens métier de démonstration ;
- effectuer automatiquement une publication distante.

### Module : Exemple

Responsabilité :

Montrer l'utilisation complète et fournir les tokens historiques de MagicToken sans les intégrer à l'API principale.

Contient notamment :

- 12 tokens métier ;
- 11 tokens numériques ;
- 6 opérateurs numériques ;
- une classe `Main` démontrant initialisation, scan, compilation et cast.

Dépendances :

- module racine `Nomos-Engine`.

Ne doit pas :

- être utilisé comme dépendance par le moteur principal ;
- définir des contrats nécessaires au fonctionnement de la bibliothèque.

---

# 6. Organisation des packages

Décrire ici les packages importants.

```text
fr.olympus.nomos
├── action
├── annotation
├── cast
├── condition
├── cost
├── language
├── math
├── register
├── resources
└── semantic
```

Le module d'exemple utilise exclusivement `fr.olympus.nomos.example` et ses sous-packages.

## Règles

Une fonctionnalité doit être placée dans le package correspondant à sa responsabilité.

Éviter les packages génériques contenant de nombreux éléments sans rapport entre eux.

Éviter notamment de transformer les packages suivants en dépôts de classes sans organisation :

```text
util
manager
misc
common
```

Avant de créer un nouveau package, vérifier qu'un package existant ne correspond pas déjà à la responsabilité concernée.

---

# 7. État actuel du projet

Cette section représente l'état actuel du projet et non son objectif futur.

## Fonctionnalités déjà implémentées

- Cycle de vie Olympus via `Nomos.init()`, `autoRegister(...)` et `getData()`.
- Enregistrement manuel et scan ClassGraph des `MagicToken` annotés.
- Tokenisation, résolution et trace immuable des occurrences reconnues.
- Compilation des propriétés, valeurs numériques, significations et coûts.
- Stratégies sémantiques interchangeables avec ordre séquentiel par défaut.
- Expressions numériques avec priorités et opérateurs extensibles.
- Conditions globales et conditions contribuées par les tokens.
- Vérification et consommation atomique des ressources.
- Sûreté concurrente des composants publics mutables.
- Module `Exemple` avec les 29 tokens historiques de MagicToken.
- Tests unitaires et de concurrence.
- Génération locale de `bundle.zip` avec artefacts, signatures et checksums.

## Fonctionnalités partiellement implémentées

- Aucune fonctionnalité partiellement implémentée connue.

## Fonctionnalités prévues mais non implémentées

- Amélioration future du langage : parenthèses, littéraux numériques et diagnostics enrichis.
- Évolutions métier futures définies par le développeur.

## Limitations connues

- Les mots inconnus sont volontairement ignorés.
- Les expressions invalides retombent sur la contribution par défaut du token cible.
- Les tokens décimaux, signes unaires et parenthèses ne sont pas intégrés nativement.
- `Nomos` est initialisable une seule fois par JVM et ne fournit ni reset ni shutdown.

## Problèmes connus

- Aucun problème bloquant connu.
- Aucune licence n'est déclarée pour le moment.
- La publication distante reste une opération manuelle hors du code.

---

# 8. Systèmes existants

## Cycle de vie Olympus

Statut : IMPLÉMENTÉ

Classes principales : `Nomos`, `NomosData`.

Responsabilité : initialiser une instance globale unique et exposer les registres centraux.

API importante : `Nomos.init()`, `Nomos.autoRegister(...)`, `Nomos.getData()`.

## Enregistrement des tokens

Statut : IMPLÉMENTÉ

Classes principales : `MagicTokenAnnotation`, `AutoRegistrar`, `RegisterType`, `TokenRegistry`.

Responsabilité : enregistrer une instance unique par définition, associer ID et alias, refuser les collisions et découvrir les classes annotées dans des packages explicites.

API importante : `TokenRegistry.register(...)`, `NomosData.registerToken(...)`, `Nomos.autoRegister(...)`.

## Langage et expressions

Statut : IMPLÉMENTÉ

Classes principales : `MagicToken`, `ValueTargetToken`, `NumericMagicToken`, `NumericOperatorToken`, `Tokenizer`, `NumericExpressionParser`.

Responsabilité : découper les phrases, résoudre les définitions et évaluer les expressions numériques.

## Compilation et sémantique

Statut : IMPLÉMENTÉ

Classes principales : `SpellActionCompiler`, `SpellAction`, `ActionContribution`, `MagicMeaning`, `SpellVector`, `SpellActionType`, `Token`, `SemanticStrategy`, `SemanticContext`, `SemanticPlan`, `ResolvedToken`.

Responsabilité : conserver la trace et le vecteur dans l'ordre lexical, faire produire un ordre d'évaluation validé par une stratégie sémantique, puis fusionner les contributions dans cet ordre.

## Conditions et lancement

Statut : IMPLÉMENTÉ

Classes principales : `SpellCondition`, `SpellValidationResult`, `SpellCastService`, `SpellCastResult`, `SpellCaster`.

Responsabilité : valider une action avant paiement et retourner un résultat explicite.

## Coûts et ressources

Statut : IMPLÉMENTÉ

Classes principales : `CostProfile`, `SpellCost`, `ResourceContainer`.

Responsabilité : calculer les coûts par ressource et garantir un paiement complet et atomique.

## Vocabulaire d'exemple

Statut : IMPLÉMENTÉ DANS LE MODULE `Exemple`

Responsabilité : démontrer l'extension de Nomos avec les 29 tokens issus de MagicToken. Ce vocabulaire ne doit pas être déplacé dans le moteur principal.

---

# 9. Décisions architecturales

> SECTION MAINTENUE PAR L'ORCHESTRATEUR.

Les décisions importantes qui peuvent influencer plusieurs fonctionnalités doivent être enregistrées ici.

Le but est d'éviter qu'un agent remette en question une décision déjà prise sans connaître son contexte.

## ADR-001 — Cycle de vie Olympus

### Décision

Nomos utilise une façade singleton initialisée exactement une fois par `Nomos.init()`. L'accès aux données et l'enregistrement automatique passent par cette façade. Aucun reset ou shutdown n'est exposé.

### Motivation

Conserver le contrat partagé par Hephaestus, Prometheus et Heracles.

### Conséquences

Les applications et les tests doivent organiser l'initialisation globale une seule fois par JVM.

### Statut

ACTIVE

## ADR-002 — Définitions de tokens uniques et exemples séparés

### Décision

Le registre conserve une instance unique par définition. Le moteur principal ne fournit aucun token métier concret ; les 29 tokens historiques résident dans `Exemple`.

### Motivation

Les classes enregistrées décrivent le langage du compilateur et sont sans état. La séparation garde Nomos générique.

### Conséquences

Les consommateurs fournissent leur propre vocabulaire annoté ou réutilisent le module d'exemple uniquement comme référence.

### Statut

ACTIVE

## ADR-003 — Collisions et atomicité

### Décision

Une collision d'ID ou d'alias lève une exception. L'enregistrement d'une définition est atomique pour tous ses noms. Un scan de plusieurs classes suit le comportement Olympus : si une classe échoue, les classes précédemment enregistrées restent présentes.

### Motivation

Éviter les remplacements silencieux tout en conservant le comportement du registrar Olympus.

### Conséquences

Les packages scannés doivent utiliser des identifiants uniques et l'appelant doit traiter les échecs de scan.

### Statut

ACTIVE

## ADR-004 — Sûreté concurrente

### Décision

Les composants publics mutables doivent accepter l'accès concurrent. Le paiement d'un `SpellCost` est une opération atomique de vérification puis consommation.

### Motivation

Permettre l'intégration dans des frameworks multithreads sans double dépense ni exposition de collections mutables internes.

### Conséquences

Les getters de collections renvoient des snapshots immuables. Toute nouvelle mutation composée doit être protégée comme une seule opération logique.

### Statut

ACTIVE

## ADR-005 — Publication locale uniquement

### Décision

Le build prépare `fr.olympus-engine:Nomos-Engine:1.0.0` et génère un bundle signé local dans `build/libs/bundle.zip`. Aucun upload distant n'est codé.

### Motivation

La publication est réalisée manuellement par le développeur.

### Conséquences

Les identifiants de signature restent dans la configuration Gradle personnelle. Aucune licence n'est déclarée tant que le développeur ne l'a pas choisie.

### Statut

ACTIVE

## ADR-006 — Stratégies sémantiques par compilateur

### Décision

`SpellActionCompiler` reçoit une `SemanticStrategy`. La stratégie séquentielle est utilisée par défaut et conserve le comportement historique. Une stratégie personnalisée reçoit les occurrences reconnues en ordre lexical et doit retourner une permutation exacte pour leur évaluation.

### Motivation

Permettre plusieurs syntaxes et langues avec un même registre de tokens, sans introduire de règles linguistiques dans le moteur ni modifier le vecteur sémantique.

### Conséquences

La trace et le vecteur restent lexicaux ; les contributions et expressions suivent le plan sémantique. Une stratégie ne peut ni ajouter, ni supprimer, ni dupliquer une occurrence. Plusieurs compilateurs utilisant différentes stratégies peuvent partager le même registre.

### Statut

ACTIVE

---

# 10. Conventions de développement

## Général

Avant de créer une nouvelle abstraction, rechercher si une abstraction équivalente existe déjà.

Cela concerne notamment :

- les services ;
- les managers ;
- les registries ;
- les factories ;
- les repositories ;
- les annotations ;
- les événements ;
- les modèles ;
- les DTO ;
- les utilitaires.

Réutiliser et étendre l'architecture existante lorsque cela est raisonnable.

Ne pas créer un second système remplissant la même responsabilité.

---

# 11. Conventions Java

Utiliser la version Java configurée par le projet.

Privilégier les fonctionnalités modernes de Java lorsqu'elles améliorent réellement la conception.

Utiliser notamment les `record` pour les objets servant principalement à transporter des données immuables.

Utiliser les `enum` pour les ensembles finis de valeurs.

Utiliser les `sealed class` et `sealed interface` lorsque la hiérarchie doit volontairement être limitée.

Utiliser le pattern matching lorsque cela améliore la lisibilité.

Utiliser `try-with-resources` pour les ressources devant être fermées.

Favoriser l'immutabilité lorsque cela est raisonnable.

Éviter le boilerplate inutile.

Éviter les getters/setters systématiques lorsqu'une meilleure abstraction peut être utilisée.

---

# 12. Rôle de l'agent principal

L'agent principal est l'ORCHESTRATEUR du projet.

Son objectif principal n'est pas d'effectuer lui-même tout le travail.

Il est responsable de :

- comprendre la demande ;
- analyser le projet existant ;
- définir l'architecture nécessaire ;
- identifier les dépendances ;
- découper le travail ;
- déléguer les tâches indépendantes ;
- coordonner les sous-agents ;
- intégrer les résultats ;
- vérifier la cohérence finale ;
- compiler ;
- tester ;
- maintenir ce fichier.

L'orchestrateur reste responsable du résultat final même lorsqu'une implémentation est réalisée par un sous-agent.

---

# 13. Analyse avant implémentation

Pour toute tâche non triviale, l'orchestrateur doit commencer par analyser :

1. les systèmes existants concernés ;
2. les classes existantes pouvant être réutilisées ;
3. les nouveaux composants éventuellement nécessaires ;
4. les dépendances entre composants ;
5. les contrats partagés nécessaires ;
6. les tâches pouvant être parallélisées ;
7. les tâches devant être exécutées séquentiellement.

L'implémentation ne doit pas commencer avant que les dépendances importantes soient comprises.

---

# 14. Architecture avant parallélisation

Lorsque plusieurs fonctionnalités doivent travailler ensemble, les contrats communs doivent être définis avant de déléguer leur implémentation.

Cela peut inclure :

- interfaces ;
- records ;
- modèles ;
- événements ;
- annotations ;
- DTO ;
- services ;
- registries ;
- formats de données.

Les sous-agents doivent respecter ces contrats.

Ils ne doivent pas inventer chacun leur propre version d'un concept partagé.

---

# 15. Politique de délégation

Pour toute tâche contenant au moins deux sous-tâches réellement indépendantes, la délégation à plusieurs sous-agents doit être privilégiée.

Lorsque les capacités de sous-agents sont disponibles, une tâche complexe ne doit pas être intégralement réalisée par l'orchestrateur si elle peut raisonnablement être parallélisée.

Exemples de tâches pouvant être parallélisées :

```text
Agent A
→ système de commandes

Agent B
→ système de rôles

Agent C
→ persistence

Agent D
→ tests
```

Exemple de mauvaise parallélisation :

```text
Agent A
→ modification de CommandManager

Agent B
→ refactor de CommandManager

Agent C
→ ajout des permissions dans CommandManager
```

Éviter que plusieurs agents modifient simultanément les mêmes fichiers.

---

# 16. Plan de délégation obligatoire

Avant de lancer plusieurs sous-agents, l'orchestrateur doit annoncer brièvement son plan.

Format recommandé :

```text
Plan de délégation

Architecture :
- ...

Agent 1 :
- responsabilité ;
- fichiers ou système concerné.

Agent 2 :
- responsabilité ;
- fichiers ou système concerné.

Agent 3 :
- responsabilité ;
- fichiers ou système concerné.

Dépendances :
- Agent 3 dépend du résultat de l'Agent 1.
- Agents 1 et 2 peuvent travailler simultanément.
```

Cela permet au développeur de comprendre comment Codex répartit le travail.

---

# 17. Instructions données aux sous-agents

Chaque sous-agent doit recevoir au minimum :

- son objectif précis ;
- son périmètre ;
- le contexte nécessaire ;
- les contrats qu'il doit respecter ;
- les systèmes existants concernés ;
- les fichiers ou packages qu'il est autorisé à modifier ;
- les dépendances qu'il peut utiliser ;
- ce qu'il ne doit pas implémenter ;
- le résultat attendu ;
- les validations qu'il doit effectuer.

Un sous-agent ne doit pas prendre seul une décision architecturale ayant un impact important sur les autres systèmes.

Dans cette situation, il doit remonter le problème à l'orchestrateur.

---

# 18. Dépendances entre tâches

L'orchestrateur doit raisonner sous forme de graphe de dépendances.

Exemple :

```text
Architecture
├── modèle Role
├── modèle Permission
└── API Command

Role
└── RoleService

RoleService + Permission
└── PermissionService

PermissionService + Command API
└── Command implementation

Toutes les implémentations
└── Tests d'intégration
```

Les tâches indépendantes peuvent être exécutées simultanément.

Une tâche dépendante ne doit pas être lancée avec de fausses hypothèses concernant une dépendance encore indéfinie.

---

# 19. Communication entre agents

L'orchestrateur est responsable de la circulation des informations entre les sous-agents.

Si un agent découvre une information pouvant affecter le travail d'autres agents :

1. il la remonte à l'orchestrateur ;
2. l'orchestrateur analyse son impact ;
3. l'orchestrateur prend ou valide la décision ;
4. les agents concernés reçoivent la nouvelle information.

Les sous-agents ne doivent pas créer silencieusement des conventions incompatibles entre eux.

---

# 20. Intégration

Lorsque les sous-agents terminent leur travail, l'orchestrateur doit :

1. récupérer les résultats ;
2. inspecter les modifications ;
3. vérifier leur compatibilité ;
4. vérifier les dépendances ;
5. résoudre les conflits ;
6. supprimer les duplications ;
7. vérifier les APIs publiques ;
8. effectuer les modifications d'intégration nécessaires.

Une tâche n'est pas terminée simplement parce que tous les sous-agents ont terminé.

---

# 21. Validation

Avant de considérer une tâche comme terminée :

- compiler les modules concernés ;
- exécuter les tests pertinents ;
- vérifier les erreurs de compilation ;
- vérifier les warnings significatifs ;
- vérifier les dépendances entre modules ;
- vérifier les régressions potentielles ;
- vérifier qu'aucun système n'a été dupliqué ;
- vérifier que l'architecture reste cohérente.

Pour une modification importante, utiliser si possible un sous-agent distinct pour effectuer une review finale.

---

# 22. Modification de AGENTS.md par l'orchestrateur

L'orchestrateur est autorisé et encouragé à mettre à jour ce fichier lorsqu'il découvre des informations durables et importantes concernant le projet.

Il doit notamment maintenir :

- l'architecture actuelle ;
- les modules ;
- les systèmes existants ;
- les fonctionnalités implémentées ;
- les limitations connues ;
- les décisions architecturales ;
- les conventions nouvelles ;
- les relations importantes entre systèmes.

Cependant, il ne doit PAS :

- supprimer une règle explicitement écrite par le développeur ;
- modifier l'objectif du projet sans demande du développeur ;
- inventer des fonctionnalités comme si elles existaient déjà ;
- enregistrer des hypothèses non vérifiées comme des faits ;
- remplacer une décision du développeur par sa propre préférence.

En cas de doute, conserver l'information existante.

---

# 23. Informations maintenues automatiquement

L'orchestrateur peut compléter cette section au cours du développement.

## Dernière analyse de l'architecture

27 septembre 2026 : transposition complète de MagicToken vers l'architecture Olympus, création du module d'exemple, intégration des conditions et validation de la sûreté concurrente.

## Composants importants identifiés

- `Nomos` et `NomosData` pour le cycle de vie et le stockage central.
- `TokenRegistry` et `AutoRegistrar` pour l'enregistrement.
- `SpellActionCompiler` comme point central du pipeline.
- `SemanticStrategy`, `SemanticContext` et `SemanticPlan` pour personnaliser l'ordre d'évaluation.
- `SpellCastService` et `ResourceContainer` pour la validation et le paiement.
- Module `Exemple` pour le vocabulaire concret.

## Relations importantes

- `NomosData` possède le registre partagé utilisé par le compilateur.
- Le registrar crée les définitions annotées puis délègue au registre.
- Le compilateur dépend du registre et du tokenizer, puis produit une `SpellAction`.
- Le compilateur soumet les occurrences résolues à sa stratégie avant d'appliquer les contributions.
- Le service de cast valide l'action et délègue le paiement atomique au conteneur de ressources.
- Le module `Exemple` dépend du module principal ; le module principal ne dépend jamais de l'exemple.

## Dette technique identifiée

- Absence actuelle de licence choisie.
- Le langage numérique reste volontairement limité et ses diagnostics pourront être enrichis.
- Les bibliothèques Olympus dupliquent encore leur infrastructure d'enregistrement ; Nomos ne crée pas de dépendance commune dans le cadre actuel.

## Refactors futurs potentiels

- Enrichir la grammaire numérique sans casser le comportement historique.
- Introduire de meilleurs diagnostics pour les mots inconnus et expressions invalides lorsque le contrat produit le permettra.
- Évaluer un module commun Olympus pour le cycle de vie et le scan seulement si l'ensemble de la suite décide de converger.

Ces éléments sont informatifs.

Un refactor potentiel ne doit pas être effectué automatiquement s'il sort du périmètre de la tâche demandée.

---

# 24. Interdictions

Les agents ne doivent pas :

- réécrire une partie importante du projet sans nécessité ;
- introduire une nouvelle dépendance sans raison ;
- changer une API publique sans analyser son impact ;
- supprimer une fonctionnalité existante pour simplifier leur travail ;
- créer un système parallèle à un système existant ;
- modifier des fichiers sans rapport avec la tâche simplement pour les nettoyer ;
- effectuer un gros refactor non demandé au milieu d'une fonctionnalité ;
- supposer qu'une classe est inutilisée sans vérification ;
- contourner une architecture existante uniquement parce qu'une autre solution semble plus simple.

---

# 25. Priorités

En cas de choix entre plusieurs solutions, appliquer cet ordre de priorité :

1. Respecter la demande du développeur.
2. Respecter les décisions documentées dans ce fichier.
3. Préserver la compatibilité avec l'existant.
4. Réutiliser l'architecture existante.
5. Maintenir une architecture cohérente.
6. Favoriser la simplicité.
7. Favoriser la maintenabilité.
8. Optimiser uniquement lorsqu'il existe une raison concrète de le faire.

---

# 26. Zone libre du développeur

> Cette section appartient au développeur.
>
> L'orchestrateur ne doit pas supprimer son contenu.

Ajouter ici toutes les informations particulières utiles au projet.

### Contraintes

- Java 25.
- Bibliothèque générique sans dépendance obligatoire envers une autre bibliothèque Olympus.
- ClassGraph est la seule dépendance fonctionnelle externe nécessaire.
- Les projets MagicToken, Hephaestus, Prometheus et Heracles servent uniquement de références et ne doivent jamais être modifiés dans le cadre de Nomos.

### Préférences d'architecture

- Respect strict du modèle d'initialisation et d'enregistrement Olympus.
- Package racine `fr.olympus.nomos`.
- Une instance unique par définition de token.
- API manuelle disponible en complément du scan annoté.
- Usage concurrent garanti.

### Fonctionnalités importantes à conserver

- Comportements fonctionnels actuels de MagicToken, notamment l'ignorance des mots inconnus et le repli des expressions invalides sur la contribution par défaut.
- Les tokens historiques doivent rester dans le module `Exemple`.
- Les conditions et types autrefois inutilisés doivent rester intégrés au pipeline.

### Informations supplémentaires

- Coordonnées préparées : `fr.olympus-engine:Nomos-Engine:1.0.0`.
- README en anglais, communications utilisateur en français.
- Publication manuelle par le développeur à partir du bundle local.
- Aucune licence choisie à ce jour.
