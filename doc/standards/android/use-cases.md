### Use Case Standards

- **Single Responsibility**: Each use case should encapsulate a single, well-defined piece of
  business logic that orchestrates interactions between multiple domain abstractions (e.g.
  repositories) to achieve a single domain outcome.
- **Naming**: Name the use case class using a verb phrase that clearly indicates the action being
  performed, e.g. `UpdateSettings`.
- **Single invoke Function**: Each use case should expose one and only one public function called
  `operator fun invoke` that performs the action. This function should accept all necessary
  parameters and return the result or outcome of the action. It may be a `suspend` function if
  needed.
