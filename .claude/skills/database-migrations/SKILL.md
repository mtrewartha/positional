---
name: Database Migrations
description: Create safe, reversible database migrations with focused changes, proper naming, version control, and zero-downtime deployment considerations. Use this skill when creating database migration files, modifying database schemas, adding or changing tables or columns, managing indexes, writing migration up/down methods, planning backwards-compatible schema changes, or working with Room migration code. Use this when working with database schema changes in migration files or Room database configuration.
---
# Database Migrations
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle database migrations.

## When to use this skill

- When creating new database migration files to modify schemas
- When implementing rollback/down methods for safe migration reversals
- When planning migrations to be small and focused on single logical changes
- When designing zero-downtime deployments with backwards-compatible schema changes
- When separating schema changes from data migrations for better rollback safety
- When creating indexes on large tables using concurrent options to avoid locks
- When naming migrations with clear, descriptive names that indicate the change
- When ensuring migrations are committed to version control and never modified after deployment

## Instructions
For details, refer to the information provided in this file:
[database migrations](../../../agent-os/standards/database/migrations.md)
