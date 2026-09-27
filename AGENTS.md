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

> SECTION À RENSEIGNER PAR LE DÉVELOPPEUR.

## Nom du projet

À compléter.

## Description

Décrire ici le projet en quelques paragraphes.

Expliquer notamment :

- à quoi sert le projet ;
- quel problème il cherche à résoudre ;
- qui est censé l'utiliser ;
- quelles sont ses fonctionnalités principales ;
- quel est son objectif à long terme.

Exemple :

Le projet est une application permettant de [...]

Son objectif principal est de [...]

L'application doit permettre de [...]

## Objectifs principaux

- À compléter.
- À compléter.
- À compléter.

## Fonctionnalités prévues

- À compléter.
- À compléter.
- À compléter.

---

# 4. Technologies utilisées

> SECTION À RENSEIGNER PAR LE DÉVELOPPEUR ET À MAINTENIR PAR L'ORCHESTRATEUR.

## Langages

- Java : version à préciser.

## Build

- Gradle / Maven : à préciser.

## Frameworks

- À compléter.

## Bibliothèques principales

- À compléter.

## Environnement

- IDE principal : IntelliJ IDEA.
- Système cible : à compléter.

---

# 5. Architecture générale

> SECTION INITIALEMENT RENSEIGNÉE PAR LE DÉVELOPPEUR.
>
> L'orchestrateur doit ensuite la maintenir lorsque l'architecture évolue.

Décrire ici les grandes parties du projet.

Exemple :

```text
Projet
├── api
│   └── API publique
│
├── core
│   └── Implémentation principale
│
├── processor
│   └── Annotation processors
│
└── application
    └── Point d'entrée de l'application
```

## Modules

### Module : À compléter

Responsabilité :

À compléter.

Contient notamment :

- ...
- ...
- ...

Dépendances :

- ...

Ne doit pas :

- ...

### Module : À compléter

Responsabilité :

À compléter.

Contient notamment :

- ...
- ...
- ...

Dépendances :

- ...

Ne doit pas :

- ...

---

# 6. Organisation des packages

Décrire ici les packages importants.

Exemple :

```text
fr.mrqsdf.project
├── command
├── role
├── permission
├── persistence
├── service
├── event
└── util
```

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

> SECTION MAINTENUE PAR L'ORCHESTRATEUR.

Cette section doit représenter l'état actuel du projet et non son objectif futur.

## Fonctionnalités déjà implémentées

- À compléter.

## Fonctionnalités partiellement implémentées

- À compléter.

## Fonctionnalités prévues mais non implémentées

- À compléter.

## Limitations connues

- À compléter.

## Problèmes connus

- À compléter.

---

# 8. Systèmes existants

> SECTION IMPORTANTE.
>
> L'orchestrateur doit maintenir cette section afin d'éviter qu'un agent recrée un système déjà existant.

Pour chaque système important, documenter rapidement son rôle et ses principaux points d'entrée.

## Exemple : système de commandes

Statut :

NON IMPLÉMENTÉ / PARTIEL / IMPLÉMENTÉ

Classes principales :

- ...

Responsabilité :

- ...

API importante :

- ...

Relations avec les autres systèmes :

- ...

## Exemple : système de rôles

Statut :

NON IMPLÉMENTÉ / PARTIEL / IMPLÉMENTÉ

Classes principales :

- ...

Responsabilité :

- ...

API importante :

- ...

Relations avec les autres systèmes :

- ...

## Exemple : système de permissions

Statut :

NON IMPLÉMENTÉ / PARTIEL / IMPLÉMENTÉ

Classes principales :

- ...

Responsabilité :

- ...

API importante :

- ...

Relations avec les autres systèmes :

- ...

---

# 9. Décisions architecturales

> SECTION MAINTENUE PAR L'ORCHESTRATEUR.

Les décisions importantes qui peuvent influencer plusieurs fonctionnalités doivent être enregistrées ici.

Le but est d'éviter qu'un agent remette en question une décision déjà prise sans connaître son contexte.

Format recommandé :

## ADR-001 — Nom de la décision

### Décision

Décrire la décision.

### Motivation

Expliquer pourquoi cette solution a été choisie.

### Conséquences

Décrire les conséquences sur le reste du projet.

### Statut

ACTIVE / REMPLACÉE / ABANDONNÉE

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

À compléter automatiquement.

## Composants importants identifiés

- À compléter automatiquement.

## Relations importantes

- À compléter automatiquement.

## Dette technique identifiée

- À compléter automatiquement.

## Refactors futurs potentiels

- À compléter automatiquement.

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

- À compléter.

### Préférences d'architecture

- À compléter.

### Fonctionnalités importantes à conserver

- À compléter.

### Informations supplémentaires

- À compléter.
