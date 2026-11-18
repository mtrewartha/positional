# Feature: [Feature Name]

## Blueprint

### Context
[1-2 paragraphs: Why does this feature exist? What problem does it solve? What business or user need drives it?]

Behavior marked _(pending POS-n)_ is intended but not yet implemented; the marker covers only the sentence it follows, the linked issue tracks the change, and the marker is removed in the same commit as the fix.

#### Glossary
[Optional. Define domain terms that the rest of the spec relies on for precise meaning. Omit if the feature has no specialized vocabulary.]
- **[Term]:** [Definition]

### Architecture
- **Where it lives:** [Module paths for the feature's domain and UI code, plus any test fixtures other modules use. Point at modules, not classes.]
- **Behavior:**
  - [A contract the feature upholds, described as a concept rather than a code symbol. Litmus test: if a rename or refactor with no behavior change would force an edit, it belongs in the code, not here.]
  - [Intended behavior that isn't implemented yet. _(pending [POS-n](https://linear.app/trewartha/issue/POS-n))_]
- **Depends on:**
  - [Optional. Another feature, linked to its spec, and what this feature relies on it for, as a concept.]
- **Depended on by:**
  - [Another feature, linked to its spec, and what it relies on from this one, as a concept. Module dependencies themselves live in the build files.]
- **Constraints:** [Security, compliance, or architectural boundaries. State as facts: "All PII fields use column-level encryption." Reserve for rules that aren't already captured by the positive architecture above.]

[When behavior changes, mark the outdated text `[DEPRECATED yyyy-mm-dd]` with the reason and the replacement, rather than deleting it, so the spec records why the feature evolved.]

## Contract

### Definition of Done
- [ ] [One observable, measurable claim. Split compound criteria so each can be verified independently. Check the box only when a test proves it.]
- [ ] [Every testable criterion has a scenario below, and every scenario has a criterion here.]

### Regression Guardrails
- [Invariant that must never break, across all future changes. State it positively and name how it's enforced when that isn't obvious, e.g. "Formatter tests pin the exact strings."]

### Scenarios
[One behavior per scenario. Every scenario has a When. Steps describe what the user or system observes, not how it's implemented: no code symbols or platform API calls. Platform identifiers that are part of the contract, such as permission names or URI schemes, are fine. Thens must be observable.]

```gherkin
Scenario: [Descriptive name]
  Given [Precondition — system state before the action]
  When [Action — what the user or system does]
  Then [Expected outcome — observable, verifiable result]
  And [Additional verification if needed]

@pending @POS-n
Scenario: [Intended behavior that isn't implemented yet]
  Given [Precondition]
  When [Action]
  Then [Expected outcome]
```
