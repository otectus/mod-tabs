package vodmordia.modtabs.api.tabs_menu;

/**
 * Pure pagination arithmetic for the tab bar, shared by {@code initScreenButtons},
 * {@code nextTabButtons} and {@code cycleTab} so all three agree on what a valid page
 * start is. Deliberately free of any Minecraft dependency so it can be unit-tested.
 *
 * <p>Terminology: sticky tabs are pinned and never paginate; the remaining
 * {@code availableSlots} page through the non-sticky tabs. Valid page starts are
 * multiples of {@code availableSlots} below {@code nonStickyCount}.
 */
public final class TabPaginationMath {

    private TabPaginationMath() {}

    /**
     * Clamp a (possibly stale) start index against the current tab set. A preserved
     * index can point past the end after the set shrinks (chest broken, tab disabled);
     * without the clamp the bar renders an empty page until the user pages manually.
     * The result is always a valid page start: floored to a page boundary and capped
     * at the last non-empty page.
     */
    public static int clampStartIndex(int startTabIndex, int nonStickyCount, int availableSlots) {
        if (availableSlots <= 0 || nonStickyCount <= 0 || startTabIndex <= 0) {
            return 0;
        }
        int lastPageStart = ((nonStickyCount - 1) / availableSlots) * availableSlots;
        int pageAligned = (startTabIndex / availableSlots) * availableSlots;
        return Math.min(pageAligned, lastPageStart);
    }

    /**
     * Advance to the next page, wrapping to 0 past the end. The {@code Math.min} term
     * shrinks the final step so the last page is fully populated (it backs the start
     * index up rather than showing a mostly-empty tail page).
     */
    public static int advanceStartIndex(int startTabIndex, int availableSlots, int nonStickyCount) {
        if (availableSlots <= 0) {
            return startTabIndex;
        }
        if (startTabIndex + availableSlots >= nonStickyCount) {
            return 0;
        }
        return startTabIndex + availableSlots
                + Math.min(nonStickyCount - availableSlots * 2 - startTabIndex, 0);
    }

    /** Page start (a multiple of {@code availableSlots}) containing the given non-sticky index. */
    public static int pageStartFor(int nonStickyIndex, int availableSlots) {
        if (availableSlots <= 0 || nonStickyIndex <= 0) {
            return 0;
        }
        return (nonStickyIndex / availableSlots) * availableSlots;
    }
}
