## Use case best practices

- **Single Responsibility**: Each use case should encapsulate a single, well-defined piece of
  business logic that represents a specific action taken by the user or on behalf of the user. Use
  cases are _not_ for arbitrary reusable business logic that doesn't directly correspond to a user
  action.
- **Naming**: Name the use case class using a verb phrase that clearly indicates the action being
  performed by or on behalf of the user, e.g. `CopyCoordinatesToClipboard`, `UpdateSettings`.
- **Single invoke Function**: Each use case should expose one and only one public function called
  `operator fun invoke` that performs the action. This function should accept all necessary
  parameters and return the result or outcome of the action. You may mark it `suspend` if needed.