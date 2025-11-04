## Android-specific testing best practices

- **Use Kotest Behavior Specs**: Use Kotest's `BehaviorSpec` style for unit tests.
- **Rely on the Test Pyramid**: Prefer cheaper, more stable, faster unit tests over slower, bigger,
  less stable forms of testing like integration tests and acceptance/end-to-end tests.
- **Prefer Robolectric over instrumented tests**: Unless something is too complex or not achievable
  with Robolectric, prefer it to instrumented tests for the sake of test speed.
- **Use Compose UI Tests**: Composables that are public in a module should be tested with Compose UI
  tests.