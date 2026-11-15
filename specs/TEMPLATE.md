# Feature: [Feature Name]

## Blueprint

### Context
[1-2 paragraphs: Why does this feature exist? What problem does it solve?
What business or user need drives it?]

### Architecture
- **Data Models:**
  - `[Model]` in `feature/[x]/src/main/kotlin/.../[Model].kt` — [What it represents]
  - [Repeat for each model; reference shared types from `core:measurement`, e.g. `Angle`,
    `GeodeticCoordinates`, rather than redefining them]
- **Domain Interfaces:**
  - `[Interface]` in `feature/[x]/src/main/kotlin/.../[Interface].kt` — [Its role in the feature]
    - `[property]: [Model]` — [What it provides]
    - `[function]([params]): [Model]` — [What it does]
  - [Repeat for each interface]
- **UI State:** `[Feature]State` exposed as `StateFlow` by `[Feature]ViewModel` in
  `feature/[x]/ui/src/main/kotlin/.../[Feature]ViewModel.kt` — [What the View renders from it]
- **Dependencies:**
  - Depends on: [services, libraries, external APIs]
  - Depended on by: [downstream consumers]
- **Constraints:** [Security, compliance, or architectural boundaries.
  State as facts: "All PII fields use column-level encryption."
  Reserve for rules that aren't already captured by the positive
  architecture above.]

## Contract

### Definition of Done
- [ ] [Observable, measurable success criterion]
- [ ] [Another criterion — each must be independently verifiable]

### Regression Guardrails
- [Invariant that must never break, across all future changes]
- [Another invariant — these survive beyond the current PBI]

### Scenarios
```gherkin
Scenario: [Descriptive name]
  Given [Precondition — system state before the action]
  When [Action — what the user or system does]
  Then [Expected outcome — observable, verifiable result]
  And [Additional verification if needed]
```
