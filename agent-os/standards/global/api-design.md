## API design best practices

- **Avoid platform-specific types in models:** This keeps models reusable across platforms.
- **Leverage the language type system and features naturally**:
    - **Model types with a magnitude and a value with a class, not a primitive**: This eliminates
      potential for ambiguity of units. For example, use a `Duration` type instead of an integer
      that represents milliseconds.
    - **Use concurrency constructs to model concurrent operations naturally**: Concurrent code is
      especially error prone, so this increases maintainability and reliability.
- **Use immutable data types**: This provides thread safety when dealing with data across threads.
- **Account for domain-specific edge cases:** For example, when modeling a person, you might add a
  `name` property. Construction of a person model would fail when a blank name was passed because
  everyone who has a name cannot have a blank name, but construction of a person with a `null` name
  would be valid because someone might not have a name (e.g. a newborn that hasn't been named yet).
