package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

// https://web.archive.org/web/20250612212826/https://minecraft.wiki/w/Dimension_type
@ReturnsSelf
public class DimensionTypeBuilder extends BuilderBase<DimensionType> {

    private static final IntProvider DEFAULT_MOB_TEST = UniformInt.of(0, 7);

    public transient OptionalLong fixedTime = OptionalLong.empty();
    public transient boolean
            hasSkyLight = true,
            hasCeiling = false,
            ultraWarm = false,
            natural = true,
            bedWorks = true,
            respawnAnchorWorks = true,
            piglinSafe = false,
            hasRaids = true
                    ;
    public transient double coordinateScale = 1D;
    public transient int
            minY = -64,
            height = 384,
            logicalHeight = 384,
            monsterSpawnBlockLimit = 7
                    ;
    public transient TagKey<Block> infiniburn = BlockTags.INFINIBURN_OVERWORLD;
    public transient ResourceLocation effectsLocation = BuiltinDimensionTypes.OVERWORLD_EFFECTS;
    public transient float ambientLight = 0F;
    public transient IntProvider monsterSpawnLightTest = DEFAULT_MOB_TEST;

    public DimensionTypeBuilder(ResourceLocation id) {
        super(id);
    }

    @Info("The day time, in ticks, the dimension is fixed at")
    public DimensionTypeBuilder fixedTime(long time) {
        fixedTime = OptionalLong.of(time);
        return this;
    }

    @Info("Marks the dimension as not having skylight")
    public DimensionTypeBuilder withNoSkyLight() {
        hasSkyLight = false;
        return this;
    }

    @Info("Marks the dimension as having a *logical* ceiling")
    public DimensionTypeBuilder withCeiling() {
        hasCeiling = true;
        return this;
    }

    @Info("Marks the dimension as being ultra warm")
    public DimensionTypeBuilder ultraWam() {
        ultraWarm = true;
        return this;
    }

    @Info("Marks the dimension as being unnatural")
    public DimensionTypeBuilder unnatural() {
        natural = false;
        return this;
    }

    @Info("Disallow sleeping in beds in the dimension")
    public DimensionTypeBuilder disableBeds() {
        bedWorks = false;
        return this;
    }

    @Info("Disallow using respawn anchors in the dimension")
    public DimensionTypeBuilder disableRespawnAnchors() {
        respawnAnchorWorks = false;
        return this;
    }

    @Info("The multiplier applied to coordinates when leaving the dimension, in the range [1e-5, 3e7]")
    public DimensionTypeBuilder coordinateScale(double scale) {
        coordinateScale = Validations.assertRange(scale, 1.0E-5F, 3.0E7, "coordinateScale");
        return this;
    }

    @Info("The minimum height at which blocks can exist in the dimension, must be a multiple of 16 and in the range [-2032, 2031]")
    public DimensionTypeBuilder minY(int minY) {
        this.minY = Validations.assertIsMultipleOf(
                Validations.assertRange(minY, DimensionType.MIN_Y, DimensionType.MAX_Y, "minY"),
                16, "minY", sourceLine
        );
        return this;
    }

    @Info("The total height in which blocks can exist within the dimension, must be a multiple of 16 and in the range [16, 4064]. The maximum build height (minY + height -1) cannot be greater than 2031")
    public DimensionTypeBuilder height(int height) {
        this.height = Validations.assertIsMultipleOf(
                Validations.assertRange(height, 16, DimensionType.Y_SIZE, "height"),
                16, "height", sourceLine
        );
        return this;
    }

    @Info("The maximum height 'natural' teleporters can bring players. Cannot be greater than height")
    public DimensionTypeBuilder logicalHeight(int logicalHeight) {
        this.logicalHeight = Validations.assertRange(logicalHeight, 0, DimensionType.Y_SIZE, "logicalHeight");
        return this;
    }

    @Info("The blocks that fires burn indefinitely on")
    public DimensionTypeBuilder infiniburn(TagKey<Block> infiniburn) {
        this.infiniburn = infiniburn;
        return this;
    }

    @Info("The previously registered special effects fo the dimension, controls the cloud height; sky type; if there is constant ambient light; and lightmap behavior")
    public DimensionTypeBuilder effects(ResourceLocation effects) {
        effectsLocation = effects;
        return this;
    }

    @Info("How much light the dimension has. 0 follows the light level while 1 has no ambient lighting")
    public DimensionTypeBuilder ambientLight(float ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    @Info("The weather-affected maximum light allowed when a mob spawns")
    public DimensionTypeBuilder monsterSpawnLightTest(IntProvider test) {
        monsterSpawnLightTest = Validations.assertRange(test, 0, 15, "monsterSpawnLightTest");
        return this;
    }

    @Info("The maximum light allowed when a mob spawns")
    public DimensionTypeBuilder monsterSpawnBlockLightLimit(int limit) {
        monsterSpawnBlockLimit = Validations.assertRange(limit, 0, 15, "monsterSpawnBlockLightLimit");
        return this;
    }

    @Info("Disables piglins and hoglins transforming into their zombified variants when in the dimension")
    public DimensionTypeBuilder safeForPiglins() {
        piglinSafe = true;
        return this;
    }

    @Info("Disable raids in the dimension")
    public DimensionTypeBuilder noRaids() {
        hasRaids = false;
        return this;
    }

    @Override
    public DimensionType createObject() {
        return new DimensionType(
                fixedTime,
                hasSkyLight,
                hasCeiling,
                ultraWarm,
                natural,
                coordinateScale,
                bedWorks,
                respawnAnchorWorks,
                Validations.validate(
                        minY,
                        m -> m + height > DimensionType.MAX_Y + 1 ? "'minY' + 'height' cannot be greater than " + (DimensionType.MAX_Y + 1) : null,
                        sourceLine
                ),
                height,
                Validations.assertNotGreaterThan(
                        logicalHeight,
                        height,
                        "logicalHeight",
                        "height",
                        sourceLine
                ),
                infiniburn,
                effectsLocation,
                ambientLight,
                new DimensionType.MonsterSettings(
                        piglinSafe,
                        hasRaids,
                        monsterSpawnLightTest,
                        monsterSpawnBlockLimit
                )
        );
    }
}
