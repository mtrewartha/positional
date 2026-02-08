### Instrumentation Tests Standards

- **Suggest Reasonable Changes to Classes**: If a reasonable refactor of a class can isolate it from
  the Android framework to improve its testability, either suggest or do the refactor.
- **Prefer Robolectric to Instrumented Tests**: If a test can be converted to use Robolectric to
  avoid slow instrumented tests on an emulator or device, offer to convert it.
