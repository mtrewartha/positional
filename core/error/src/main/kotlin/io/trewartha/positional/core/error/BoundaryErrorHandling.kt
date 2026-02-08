package io.trewartha.positional.core.error

/**
 * Opt-in annotation for functions that perform broad exception catching at system boundaries.
 *
 * There are only two valid use sites for functions annotated with this:
 * 1. Code that directly wraps third-party code that might throw unknown exceptions
 * 2. Code at the Android or UI boundary that is the last line of defense before an exception can
 *    take down the app
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "There are only two generally appropriate categories of use sites for this function (or any that " +
            "catches the high-level Exception or Throwable types):\n\n" +
            "1. Code that directly wraps third-party code that might throw unknown exceptions\n\n" +
            "2. Code at the Android boundary or UI boundary that is the last line of defense before an exception can " +
            "take down the app\n\n" +
            "If you find yourself using (or wanting to use) this code anywhere else, you should narrow the scope of what " +
            "you are willing to catch and then use an explicit 'try' with 'catch' blocks for each exception type you " +
            "want to handle."
)
public annotation class BoundaryErrorHandling