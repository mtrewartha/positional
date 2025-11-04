## View model best practices

- View models should expose their data via `StateFlow` instances.
- View models should expose an `onEvent` function that receives instances of a sealed marker
  interface that represents view events the view model handles. Views should call this function when
  the corresponding event occurs in the view. This function should either immediately handle the
  event or launch a coroutine to handle it in a scope corresponding to the view model's lifecycle.