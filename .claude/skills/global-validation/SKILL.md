---
name: Global Validation
description: Implement validation on server-side for security, client-side for UX, failing early with specific error messages, using allowlists over blocklists, checking types/formats/ranges, sanitizing input to prevent injection attacks, validating business rules, and applying validation consistently across all entry points. Use this skill when implementing input validation, creating validation logic, providing error messages to users, preventing injection attacks, validating business rules, or working with any code that accepts user input or external data. Use this when writing validation code in any .kt files.
---
# Global Validation
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle global validation.

## When to use this skill

- When implementing server-side validation for security and data integrity (never trust client-side alone)
- When adding client-side validation to provide immediate user feedback
- When validating input early and rejecting invalid data before processing
- When providing clear, field-specific error messages that help users correct their input
- When defining what is allowed (allowlists) rather than blocking everything not wanted (blocklists)
- When checking data types, formats, ranges, and required fields systematically
- When sanitizing user input to prevent injection attacks (SQL, XSS, command injection)
- When validating business rules at the appropriate application layer
- When ensuring validation is applied consistently across all entry points

## Instructions
For details, refer to the information provided in this file:
[global validation](../../../agent-os/standards/global/validation.md)
