# General Rules

- **Consistent Project Structure**: Organize files and directories in a predictable, logical
  structure that team members can navigate easily
- **Clear Documentation**: Maintain up-to-date README files that contain high-level overviews of
  modules or libraries, including how to use them.
- **Version Control Best Practices**:
  - Use [conventional commit](https://www.conventionalcommits.org/en/v1.0.0/) messages
  - When creating a pull/merge request, add a meaningful title and description with a high-level
    overview of the changes.
- **Environment Configuration**: Use environment variables for configuration; never commit secrets
  or API keys to version control
- **Dependency Management**: Keep dependencies up-to-date and minimal; document why major
  dependencies are used
- **Code Review Process**: Establish a consistent code review process with clear expectations for
  reviewers and authors
- **Testing Requirements**: Define what level of testing is required before merging (unit tests,
  integration tests, etc.)
- **Feature Flags**: Use feature flags for incomplete features rather than long-lived feature
  branches
- **Changelog Maintenance**: Keep a changelog or release notes to track significant changes and
  improvements
