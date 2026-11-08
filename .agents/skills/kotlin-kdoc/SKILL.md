---
name: kotlin-kdoc
description: "Use when writing, editing, or reviewing KDoc or documentation comments (/** ... */) on Kotlin types, properties, or functions, including block tags such as @param, @return, @throws, @receiver, and @sample."
---

# KDoc Rules

- **Coverage:** Public types, functions, and properties require KDoc, except overrides (see below). KDoc is optional elsewhere, but any KDoc you write follows these rules.
- **Class docs:** Start with the class's purpose. If the class is mainly an implementation of an interface, lead with that and describe what makes it distinct. Put examples (`@sample` or code blocks) here only if they show construction or configuration; other examples belong on the relevant property or function.
- **Property docs:** Describe what the property means, including semantics not obvious from its name or type, and for configuration options, how it affects behavior. Never describe a property in terms of itself: for `sampleRate`, not "Sample rate" but "Rate in Hz at which audio is processed."
- **Function docs:** Open with a third-person verb clause ("Returns the absolute value of the given [number]."); add detail as full sentences after a blank line. Describe parameters, the receiver, and the return value in that text, linking each parameter where it is mentioned (`[number]`). Use `@param`, `@receiver`, or `@return` only when a description is too long to fit the flow of the main text ([Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html#documentation-comments)). Document each exception type the function can throw, and when, with `@throws`. Never use `@author`.
- **Block tag layout:** When tags are used, each tag type forms one group with no blank lines inside it; separate groups with a single blank line. Order: `@since`, `@constructor`, `@receiver`, `@param`, `@property`, `@throws`, `@return`, `@sample`, `@see`.
- **Samples:** `@sample` takes the fully qualified name of a function whose body Dokka embeds, and only renders if that file is registered as a samples source in the Dokka configuration. Use `@sample` only when the project has a samples source registered with Dokka, and put sample functions there. Otherwise, write the example as a fenced `kotlin` code block in the KDoc, which renders in both the IDE and Dokka.
- **Descriptive, not prescriptive:** Document behavior, semantics, and constraints — never direct consumers' architecture (no DI wiring, no "inject X", no "you should/must"). State constraints as properties of the code:
  - "Returns the same instance on every call", not "Provide this as a singleton in your DI graph"
  - "Not safe for concurrent use", not "You must guard every call with a lock"

  Wiring and setup examples belong in a README or other usage docs.
- **Contract, not mechanism:** Describe observable behavior, not implementation ("Backed by a `LinkedHashMap`", "Delegates to an internal cache"); internals may change without notice.
- **Evergreen:** Describe the code as it is. Never mention the bug a change fixed, review feedback, rejected alternatives, or previous behavior; that belongs in commits and pull requests.
- **Overrides:** Omit KDoc on overrides unless it adds public-facing context beyond the inherited doc, which Dokka shows automatically.
- **Concision:** Cut filler: "This function is used to calculate the sum of two numbers" becomes "Calculates the sum of two numbers." Drop leading articles in phrases: "Sample rate in Hz at which audio is processed.", not "The sample rate in Hz at which the audio is processed."
- **Punctuation:** Capitalize the opening line and every block-tag description, and end each with a period, whether it is a noun phrase ("Rate in Hz at which audio is processed.") or a verb phrase ("Generates random identifiers."). Detail paragraphs are full sentences with normal punctuation. Keep language formal, with no emojis.

## Example

```kotlin
/**
 * Resamples PCM [samples] captured at [sourceRate] Hz to [targetRate] Hz using this resampler's filter settings.
 *
 * Returns a new array of length `samples.size * targetRate / sourceRate`, rounded down; [samples] is not modified.
 *
 * @throws IllegalArgumentException If either rate is not positive.
 */
fun Resampler.resample(samples: FloatArray, sourceRate: Int, targetRate: Int): FloatArray
```
