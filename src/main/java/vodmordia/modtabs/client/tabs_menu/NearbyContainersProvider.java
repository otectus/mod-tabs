package vodmordia.modtabs.client.tabs_menu;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import vodmordia.modtabs.api.tabs_menu.DynamicTabProvider;
import vodmordia.modtabs.api.tabs_menu.TabBase;
import vodmordia.modtabs.config.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Discovers nearby container blocks around the player each screen-init and contributes one
 * {@link NearbyContainerTab} per pos. Detection is driven by
 * {@link BlockState#getMenuProvider}, so any block that opens a menu on right-click —
 * vanilla or modded — is picked up without a hardcoded list.
 *
 * Also exposes {@link #hasContainerSetChanged(Player)} so a tick handler can detect
 * mid-screen world changes (chest broken/placed) and trigger a re-init, rather than the
 * bar going stale until the user closes and reopens the screen.
 */
public class NearbyContainersProvider implements DynamicTabProvider {

    // Snapshot of positions emitted in the most recent contribute() call. Used by the
    // tick-driven change detector to decide if a re-init is needed.
    private static volatile Set<BlockPos> lastEmittedPositions = Collections.emptySet();

    @Override
    public void contribute(Player player, Screen screen, List<TabBase> out) {
        if (!Config.Baked.nearbyContainersTabEnabled) {
            lastEmittedPositions = Collections.emptySet();
            return;
        }
        if (player == null || !player.isAlive()) return;
        Level level = player.level();
        if (level == null) return;

        List<BlockPos> positions = scanPositions(player, level);
        // Stable order: by encoded position. Without this, tabs would dance around as iter
        // order changes (it doesn't right now, but pre-sorting makes the contract explicit).
        positions.sort(Comparator.comparingLong(BlockPos::asLong));
        for (BlockPos pos : positions) {
            out.add(new NearbyContainerTab(pos));
        }
        lastEmittedPositions = new HashSet<>(positions);
    }

    /**
     * Re-scan and report whether the current valid-container set differs from the
     * positions emitted at the last {@link #contribute} call. Cheap enough to call from
     * a throttled tick handler (one cube scan, no tab allocations).
     */
    public static boolean hasContainerSetChanged(Player player) {
        if (!Config.Baked.nearbyContainersTabEnabled) {
            // If we'd previously emitted tabs and the feature was just disabled, the
            // current bar is stale — report changed so the screen re-inits and drops them.
            return !lastEmittedPositions.isEmpty();
        }
        if (player == null || !player.isAlive()) return false;
        Level level = player.level();
        if (level == null) return false;

        List<BlockPos> positions = scanPositions(player, level);
        if (positions.size() != lastEmittedPositions.size()) return true;
        for (BlockPos pos : positions) {
            if (!lastEmittedPositions.contains(pos)) return true;
        }
        return false;
    }

    /**
     * Single-source-of-truth scan used by both {@link #contribute} (emit tabs) and
     * {@link #hasContainerSetChanged} (change detection). Returns immutable {@link BlockPos}
     * instances ready to be stored.
     *
     * <p>Instead of cube-scanning {@code (2r+1)³} block states (35,937 lookups at range 16,
     * twice a second while a tabbed screen is open), we walk the block-entity maps of the
     * few chunks the radius touches — virtually every openable container is a block entity,
     * so this reduces the scan to a handful of map entries. Known limitation, accepted
     * deliberately: blocks with a MenuProvider but no block entity (crafting table,
     * grindstone, anvil, stonecutter, …) no longer get tabs; they are stateless
     * workstations, not containers, and covering them would reinstate the cube scan.
     */
    private static List<BlockPos> scanPositions(Player player, Level level) {
        int range = Math.max(1, Config.Baked.nearbyContainersTabRange);
        boolean sightCheck = Config.Baked.nearbyContainersTabRequireLineOfSight;

        Vec3 eye = player.getEyePosition();
        BlockPos origin = player.blockPosition();
        double rangeSq = (double) range * range;

        List<BlockPos> found = new ArrayList<>();

        int minChunkX = (origin.getX() - range) >> 4;
        int maxChunkX = (origin.getX() + range) >> 4;
        int minChunkZ = (origin.getZ() - range) >> 4;
        int maxChunkZ = (origin.getZ() + range) >> 4;

        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {
                // create=false: an unloaded border chunk yields null (a tab may appear one
                // refresh cycle late while sprinting — cosmetic). Level.getChunk(int,int)
                // must NOT be used here; on the client it returns empty placeholder chunks.
                if (!(level.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, false)
                        instanceof LevelChunk chunk)) {
                    continue;
                }
                for (BlockPos pos : chunk.getBlockEntities().keySet()) {
                    if (distSqToEye(eye, pos) > rangeSq) continue;

                    BlockState state = chunk.getBlockState(pos);
                    if (state.isAir()) continue;
                    // Any block that yields a MenuProvider on right-click is a container as
                    // far as we're concerned. Covers vanilla chests/barrels/shulker boxes/
                    // hoppers/dispensers/droppers/brewing stands/etc. and any modded block
                    // entity that follows the standard right-click-to-open pattern.
                    if (state.getMenuProvider(level, pos) == null) continue;

                    // Double chests: emit exactly one tab per pair, chosen deterministically
                    // (the half with the smaller encoded position) so the emitted pos never
                    // flips between consecutive scans — chunk-map iteration order is not
                    // stable across chunks, and a flip would make hasContainerSetChanged
                    // report phantom changes for a pair straddling a chunk border.
                    if (state.hasProperty(BlockStateProperties.CHEST_TYPE)) {
                        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);
                        if ((type == ChestType.LEFT || type == ChestType.RIGHT)
                                && partnerRepresentsPair(level, eye, rangeSq, pos,
                                        otherChestHalf(pos, state, type))) {
                            continue;
                        }
                    }

                    // Vanilla "chest is blocked" rule: opaque block (or cat) on top makes it
                    // un-openable. Honour the same gate so we don't emit a tab that produces
                    // no menu when clicked.
                    if (state.getBlock() instanceof ChestBlock
                            && ChestBlock.isChestBlockedAt(level, pos)) {
                        continue;
                    }

                    if (sightCheck && !hasLineOfSight(level, player, eye, pos)) {
                        continue;
                    }

                    found.add(pos.immutable());
                }
            }
        }
        return found;
    }

    /** Squared distance from the eye position to the centre of {@code pos}, allocation-free. */
    private static double distSqToEye(Vec3 eye, BlockPos pos) {
        double dx = pos.getX() + 0.5 - eye.x;
        double dy = pos.getY() + 0.5 - eye.y;
        double dz = pos.getZ() + 0.5 - eye.z;
        return dx * dx + dy * dy + dz * dz;
    }

    /**
     * True when the OTHER half of a double chest should represent the pair instead of
     * {@code pos}: it has the smaller encoded position, it is itself within range, and it
     * is still the matching chest half (an unloaded partner chunk reads as air, in which
     * case {@code pos} takes over so the pair doesn't vanish).
     */
    private static boolean partnerRepresentsPair(Level level, Vec3 eye, double rangeSq,
            BlockPos pos, BlockPos other) {
        if (other.asLong() >= pos.asLong()) return false;
        if (distSqToEye(eye, other) > rangeSq) return false;
        BlockState otherState = level.getBlockState(other);
        if (!otherState.hasProperty(BlockStateProperties.CHEST_TYPE)) return false;
        ChestType otherType = otherState.getValue(BlockStateProperties.CHEST_TYPE);
        return otherType == ChestType.LEFT || otherType == ChestType.RIGHT;
    }

    private static BlockPos otherChestHalf(BlockPos pos, BlockState state, ChestType type) {
        Direction facing = state.getValue(ChestBlock.FACING);
        Direction toOther = (type == ChestType.LEFT) ? facing.getClockWise() : facing.getCounterClockWise();
        return pos.relative(toOther).immutable();
    }

    private static boolean hasLineOfSight(Level level, Player player, Vec3 eye, BlockPos target) {
        Vec3 targetCentre = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
        BlockHitResult result = level.clip(new ClipContext(
                eye, targetCentre, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (result.getType() != HitResult.Type.BLOCK) return true;
        return result.getBlockPos().equals(target);
    }
}
