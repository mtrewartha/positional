### Shell Script Standards

- **Shebang**: Always start shell scripts with a shebang line to specify the interpreter, e.g.
  `#!/bin/bash`
- **Interoperability**: Ensure scripts are compatible with POSIX standards, macOS, Linux, and
  Windows whenever possible.
- **Comments**: Use comments to explain the purpose of the script and any complex logic.
- **Variable Naming**: Use descriptive variable names and upper snake casing.
- **Quoting**: Always quote variables to prevent word splitting and globbing issues.
- **Indentation**: Use consistent indentation of 2 spaces for better readability.
- **Functions**: Use functions to encapsulate reusable code blocks.
- **Exit Codes**: Use appropriate exit codes to indicate success or failure.
- **Validation**: Validate user input values and pre-conditions and fail early with a clear message
  if they are unacceptable.
- **Logging**: Add logging to track the script's execution. When logging errors, use `stderr`.
- **Documentation**: Provide usage instructions and examples in the script header or a separate
  README file.
- **Testing**: Test scripts in different environments to ensure compatibility and reliability.
