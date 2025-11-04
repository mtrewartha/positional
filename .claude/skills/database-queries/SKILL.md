---
name: Database Queries
description: Write safe, efficient database queries using parameterized statements to prevent SQL injection, eager loading to avoid N+1 queries, strategic indexing, transactions for consistency, and caching for performance. Use this skill when writing Room @Query methods, implementing DAO interfaces, preventing SQL injection with parameterized queries, optimizing query performance with joins and indexes, wrapping related operations in transactions, setting query timeouts, caching expensive queries, or working with any database query code. Use this when working with Room DAO files or query implementations.
---
# Database Queries
This Skill provides Claude Code with specific guidance on how to adhere to coding standards as they
relate to how it should handle database queries.

## When to use this skill

- When writing Room @Query methods in DAO interfaces to access database data
- When preventing SQL injection by using parameterized queries instead of string interpolation
- When avoiding N+1 queries through eager loading or joins to fetch related data efficiently
- When selecting only needed columns rather than using SELECT * for better performance
- When adding indexes to columns used in WHERE, JOIN, and ORDER BY clauses
- When wrapping related database operations in transactions to maintain data consistency
- When implementing timeouts to prevent runaway queries from impacting performance
- When caching results of complex or frequently-run queries when appropriate

## Instructions
For details, refer to the information provided in this file:
[database queries](../../../agent-os/standards/database/queries.md)
