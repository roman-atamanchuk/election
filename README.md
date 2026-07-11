# Elections Manager

A JavaFX desktop application for managing Irish election data — politicians, elections, and candidacy records — built around custom, hand-written data structures rather than the Java Collections framework.

## Features

- Add, store, and persist **politicians** and **elections** (XML persistence via `XMLEncoder`/`XMLDecoder`)
- Link politicians to elections as candidate entries, tracking votes per election
- **O(1) lookup** by ID or name using a custom hash table (`CustomHashTable`)
- Partial-name and filter searches (by county, by party) via linear search
- **Selection sort** for ranking candidates by votes and politicians by name
- JavaFX UI (`primary`/`secondary` views) for interacting with the data

## Custom Data Structures

Implemented from scratch under `util/`:

- `CustomLinkedList<T>` / `Node<T>` — singly linked list backing the hash table buckets
- `CustomHashTable<T>` — string-keyed hash table with custom hash function, used for instant politician/election lookup
- `SimpleList<T>` — array-backed list used for master records and per-election candidate entries

## Tech Stack

Java 21 · JavaFX 21 · Maven · JUnit 5

## Project Structure

```
src/main/java/org/example/
  App.java                      JavaFX entry point
  PrimaryController.java / SecondaryController.java
  controller/ElectionController.java   Core logic: search, sort, persistence
  model/                        Politician, Election, ElectionType, CandidateEntry
  util/                         CustomHashTable, CustomLinkedList, SimpleList, Node
src/main/resources/org/example/  FXML views
src/test/java/                   JUnit tests for ElectionController
```

## Running

```bash
mvn javafx:run
```

## Testing

```bash
mvn test
```

Covers adding politicians/elections, and the search/sort/filter operations on `ElectionController`.
