---
name: Database Models
description: Design database models with clear naming, appropriate data types, proper constraints, timestamps for auditing, indexed foreign keys, and validation at multiple layers. Use this skill when creating Room entity classes, defining database table structures, choosing data types for columns, adding constraints like NOT NULL or UNIQUE, defining foreign key relationships with cascade behaviors, adding indexes for performance, implementing validation logic, or working with @Entity annotated classes. Use this when working with database entity definitions.
---
# Database Models
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle database models.

## When to use this skill

- When creating Room @Entity classes that represent database tables
- When choosing appropriate data types that match data purpose and size requirements
- When adding database constraints (NOT NULL, UNIQUE, foreign keys) to enforce data integrity
- When including created and updated timestamps on tables for auditing and debugging
- When defining foreign key relationships with appropriate cascade behaviors
- When adding indexes on foreign key columns and frequently queried fields
- When implementing validation at both model and database levels for defense in depth
- When balancing normalization with practical query performance needs

## Instructions
For details, refer to the information provided in this file:
[database models](../../../agent-os/standards/database/models.md)
