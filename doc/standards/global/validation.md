### Validation Standards

- **Client-Side for UX**: Use client-side validation to provide immediate user feedback, but ensure
  duplicate checks exist server-side.
- **Fail Early**: Validate input as early as possible and reject invalid data before processing.
- **Specific Error Messages**: Provide clear, field-specific error messages that help users correct
  their input.
- **Allowlists Over Blocklists**: When possible, define what is allowed rather than trying to block
  everything that's not.
- **Type and Format Validation**: Check data types, formats, ranges, and required fields
  systematically.
- **Sanitize Input**: Sanitize user input to prevent injection attacks (SQL, XSS, command
  injection).
- **Business Rule Validation**: Validate business rules (e.g., sufficient balance, valid dates) at
  the appropriate application layer.
- **Consistent Validation**: Apply validation consistently across all entry points (UI forms, API
  endpoints, background jobs).
