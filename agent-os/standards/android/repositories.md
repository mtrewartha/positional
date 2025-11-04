## Repository best practices

- Repository implementations should depend on data source abstractions.
- Data source implementations can depend on libraries or frameworks like Room, Retrofit, etc...
- Functions that get data should have names that start with "get".
- Functions that expose `Flow`s of data should have names that start with "get" and end with
  "Flow" so that callers can distinguish functions that return raw data from functions that return
  Flows that expose data.
- Functions that create data should have names that start with "create".
- Functions that update data should have names that start with "update".
- Functions that delete data should have names that start with "delete".
- All functions that `suspend` should perform their work on the default dispatcher and all exposed
  `Flow`s should "flow on" the default dispatcher to keep work off the main thread.