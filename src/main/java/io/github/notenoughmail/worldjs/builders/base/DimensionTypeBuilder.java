package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.OptionalLong;

// TODO: 1.1.0 | Docs
@ReturnsSelf
public class DimensionTypeBuilder extends BuilderBase<DimensionType> {

    public static final DimensionType.MonsterSettings DEFAULT_MONSTER_SETTINGS = new DimensionType.MonsterSettings(
            false,
            true,
            UniformInt.of(0, 7),
            0
    );

    public transient OptionalLong fixedTime = OptionalLong.empty();
    public transient boolean
            hasSkyLight = true,
            hasCeiling = false,
            ultraWarm = false,
            natural = true,
            bedWorks = true,
            respawnAnchorWorks = true;
    public transient double coordinateScale = 1D;
    public transient int
            minY = -64,
            height = 384,
            logicalHeight = 384;
    public transient TagKey<Block> infiniburn = BlockTags.INFINIBURN_OVERWORLD;
    public transient ResourceLocation effectsLocation = BuiltinDimensionTypes.OVERWORLD_EFFECTS;
    public transient float ambientLight = 0F;
    public transient DimensionType.MonsterSettings monsterSettings = DEFAULT_MONSTER_SETTINGS;

    public DimensionTypeBuilder(ResourceLocation id) {
        super(id);
    }

    public DimensionTypeBuilder fixedTime(long time) {
        fixedTime = OptionalLong.of(time);
        return this;
    }

    public DimensionTypeBuilder withNoSkyLight() {
        hasSkyLight = false;
        return this;
    }

    public DimensionTypeBuilder withCeiling() {
        hasCeiling = true;
        return this;
    }

    public DimensionTypeBuilder ultraWam() {
        ultraWarm = true;
        return this;
    }

    public DimensionTypeBuilder unnatural() {
        natural = false;
        return this;
    }

    public DimensionTypeBuilder disableBeds() {
        bedWorks = false;
        return this;
    }

    public DimensionTypeBuilder disableRespawnAnchors() {
        respawnAnchorWorks = false;
        return this;
    }

    public DimensionTypeBuilder coordinateScale(double scale) {
        coordinateScale = Validations.assertRange(scale, 1.0E-5F, 3.0E7, "coordinateScale");
        return this;
    }

    public DimensionTypeBuilder minY(int minY) {
        this.minY = Validations.validate(
                Validations.assertRange(minY, DimensionType.MIN_Y, DimensionType.MAX_Y, "minY"),
                i -> i % 16 == 0 ? null : "'minY' must be a multiple of 16!",
                sourceLine
        );
        return this;
    }

    public DimensionTypeBuilder height(int height) {
        this.height = Validations.validate(
                Validations.assertRange(height, 16, DimensionType.Y_SIZE, "height"),
                i -> i % 16 == 0 ? null : "'height' must be a multiple of 16!",
                sourceLine
        );
        return this;
    }

    public DimensionTypeBuilder logicalHeight(int logicalHeight) {
        this.logicalHeight = Validations.assertRange(logicalHeight, 0, DimensionType.Y_SIZE, "logicalHeight");
        return this;
    }

    public DimensionTypeBuilder infiniburn(TagKey<Block> infiniburn) {
        this.infiniburn = infiniburn;
        return this;
    }

    public DimensionTypeBuilder effects(ResourceLocation effects) {
        effectsLocation = effects;
        return this;
    }

    public DimensionTypeBuilder ambientLight(float ambientLight) {
        this.ambientLight = ambientLight;
        return this;
    }

    public DimensionTypeBuilder monsterSettings(DimensionType.MonsterSettings settings) {
        Validations.assertRange(settings.monsterSpawnLightTest(), 0, 15, "monsterSettings.monsterSpawnLightTest");
        Validations.assertRange(settings.monsterSpawnBlockLightLimit(), 0, 15, "monsterSettings.monsterSpawnBlockLightLimit");
        monsterSettings = settings;
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
                Validations.validate(
                        logicalHeight,
                        h -> h > height ? "'logicalHeight' cannot be higher than 'height'" : null,
                        sourceLine
                ),
                infiniburn,
                effectsLocation,
                ambientLight,
                monsterSettings
        );
    }
}
