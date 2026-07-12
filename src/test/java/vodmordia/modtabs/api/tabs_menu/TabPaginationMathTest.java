package vodmordia.modtabs.api.tabs_menu;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static vodmordia.modtabs.api.tabs_menu.TabPaginationMath.advanceStartIndex;
import static vodmordia.modtabs.api.tabs_menu.TabPaginationMath.clampStartIndex;
import static vodmordia.modtabs.api.tabs_menu.TabPaginationMath.pageStartFor;

class TabPaginationMathTest {

    // --- clampStartIndex: the restored-page-index fix ---

    @Test
    void clampKeepsInRangeStarts() {
        // 8 non-sticky tabs, 3 slots: any start up to 8-3=5 shows a full page.
        assertEquals(0, clampStartIndex(0, 8, 3));
        assertEquals(3, clampStartIndex(3, 8, 3));
        // Back-shifted starts from advanceStartIndex are legitimate and must survive.
        assertEquals(4, clampStartIndex(4, 8, 3));
        assertEquals(5, clampStartIndex(5, 8, 3));
    }

    @Test
    void clampSnapsShrunkenSetToFullLastPage() {
        // Player was on page start 6 of 8 tabs; 5 chests broke, leaving 3 tabs.
        // Old behavior: startTabIndex=6 with 3 tabs → empty bar page.
        assertEquals(0, clampStartIndex(6, 3, 3));
        // 4 tabs remain, 3 slots: snap to 1 so the last page (tabs 1-3) is full.
        assertEquals(1, clampStartIndex(6, 4, 3));
        // 8 tabs, start past the last full page start (5): snapped back to it.
        assertEquals(5, clampStartIndex(6, 8, 3));
        assertEquals(5, clampStartIndex(40, 8, 3));
    }

    @Test
    void clampHandlesDegenerateInputs() {
        // Sticky tabs fill the whole page → no paginating slots.
        assertEquals(0, clampStartIndex(5, 8, 0));
        assertEquals(0, clampStartIndex(5, 8, -1));
        // Empty non-sticky set.
        assertEquals(0, clampStartIndex(5, 0, 3));
        // Negative / zero index.
        assertEquals(0, clampStartIndex(-2, 8, 3));
        assertEquals(0, clampStartIndex(0, 8, 3));
    }

    // --- advanceStartIndex: the next-page chevron ---

    @Test
    void advanceMovesOnePageForward() {
        // 9 tabs, 3 slots: 0 → 3 → 6 → wrap to 0
        assertEquals(3, advanceStartIndex(0, 3, 9));
        assertEquals(6, advanceStartIndex(3, 3, 9));
        assertEquals(0, advanceStartIndex(6, 3, 9));
    }

    @Test
    void advanceBacksUpSoLastPageIsFull() {
        // 7 tabs, 5 slots: from 0, a naive advance to 5 would show only tabs 5-6.
        // The min-term backs the start up to 2 so the last page shows tabs 2-6.
        assertEquals(2, advanceStartIndex(0, 5, 7));
        // And from there, the next advance wraps.
        assertEquals(0, advanceStartIndex(2, 5, 7));
    }

    @Test
    void advanceWrapsWhenEverythingFits() {
        // 3 tabs, 5 slots: single page, always wraps to 0.
        assertEquals(0, advanceStartIndex(0, 5, 3));
    }

    @Test
    void advanceNoOpsWithNoSlots() {
        assertEquals(4, advanceStartIndex(4, 0, 9));
    }

    // --- pageStartFor: cycle-keybind targeting ---

    @Test
    void pageStartContainsTheIndex() {
        assertEquals(0, pageStartFor(0, 3));
        assertEquals(0, pageStartFor(2, 3));
        assertEquals(3, pageStartFor(3, 3));
        assertEquals(3, pageStartFor(5, 3));
        assertEquals(6, pageStartFor(6, 3));
    }

    @Test
    void pageStartWithStickyHeavyBar() {
        // 1 paginating slot (sticky tabs fill the rest): every index is its own page.
        assertEquals(4, pageStartFor(4, 1));
    }

    @Test
    void pageStartHandlesDegenerateInputs() {
        assertEquals(0, pageStartFor(4, 0));
        assertEquals(0, pageStartFor(-1, 3));
    }

    // --- cross-helper consistency: cycling onto a page then clamping must agree ---

    @Test
    void advanceResultIsAlwaysAValidClampedStart() {
        for (int count = 1; count <= 12; count++) {
            for (int slots = 1; slots <= 6; slots++) {
                int start = 0;
                for (int i = 0; i < 5; i++) {
                    start = advanceStartIndex(start, slots, count);
                    assertEquals(start, clampStartIndex(start, count, slots),
                            "advance produced an invalid page start for count=" + count + " slots=" + slots);
                }
            }
        }
    }
}
