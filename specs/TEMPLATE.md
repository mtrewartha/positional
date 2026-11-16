# Feature: [Feature Name]

## Blueprint

### Context
[1-2 paragraphs: Why does this feature exist? What problem does it solve?
What business or user need drives it?]

#### Glossary
[Optional. Define domain terms that the rest of the spec relies on for precise meaning.
Omit if the feature has no specialized vocabulary.]
- **[Term]:** [Definition]

### Architecture
- **Where it lives:** [Module paths for the feature's domain and UI code, plus any test fixtures
  other modules use. Point at modules, not classes.]
- **Behavior:**
  - [A contract the feature upholds, described as a concept rather than a code symbol.
    Litmus test: if a rename or refactor with no behavior change would force an edit, it belongs
    in the code, not here.]
- **Depended on by:**
  - [Another feature, and what it relies on from this one, as a concept. Module dependencies
    themselves live in the build files.]
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
