package io.github.notenoughmail.worldjs.builders.base;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.types.assist.ClimateParameterListBuilder;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.Util;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ReturnsSelf
public class NoiseGeneratorSettingsBuilder extends BuilderBase<NoiseGeneratorSettings> {

    public transient NoiseSettings noiseSettings = NoiseSettings.OVERWORLD_NOISE_SETTINGS;
    @Nullable
    public transient BlockState defaultBlock, defaultFluid;
    @Nullable
    public transient NoiseRouter noiseRouter;
    public transient SurfaceRules.RuleSource surfaceRule = SurfaceRuleData.overworld();
    public transient List<Climate.ParameterPoint> spawnTarget = new ArrayList<>();
    public transient int seaLevel = 63;
    public transient boolean
            disableMobGeneration = false,
            aquifers = true,
            oreVeins = true,
            legacyRandomSource = false
                    ;

    public NoiseGeneratorSettingsBuilder(ResourceLocation id) {
        super(id);
    }

    public NoiseGeneratorSettingsBuilder noiseSettings(
            int minY,
            int height,
            int noiseSizeHorizontal,
            int noiseSizeVertical
    ) {
        Validations.assertIsMultipleOf(
                Validations.assertRange(minY, DimensionType.MIN_Y, DimensionType.MAX_Y, "noiseSettings.minY"),
                16, "noiseSettings.minY", sourceLine
        );
        Validations.assertIsMultipleOf(
                Validations.assertRange(height, 0, DimensionType.Y_SIZE, "noiseSettings.height"),
                16, "noiseSettings.height", sourceLine
        );
        Validations.assertNotGreaterThan(
                minY + height,
                DimensionType.MAX_Y + 1,
                "noiseSettings.minY' + 'noiseSettings.height",
                null,
                sourceLine
        );
        noiseSettings = new NoiseSettings(
                minY,
                height,
                Validations.assertRange(noiseSizeHorizontal, 1, 4, "noiseSettings.noiseSizeHorizontal"),
                Validations.assertRange(noiseSizeVertical, 1, 4, "noiseSettings.noiseSizeVertical")
        );
        return this;
    }

    public NoiseGeneratorSettingsBuilder defaults(BlockState block, BlockState fluid) {
        defaultBlock = block;
        defaultFluid = fluid;
        return this;
    }

    public NoiseGeneratorSettingsBuilder noiseRouter(NoiseRouter router) {
        noiseRouter = router;
        return this;
    }

    public NoiseGeneratorSettingsBuilder ruleSource(SurfaceRules.RuleSource source) {
        surfaceRule = source;
        return this;
    }

    public NoiseGeneratorSettingsBuilder addSpawnTarget(Consumer<ClimateParameterListBuilder.Entry> builder) {
        spawnTarget.add(Util.make(
                ClimateParameterListBuilder.entry(null),
                builder
        ).point().toPoint());
        return this;
    }

    public NoiseGeneratorSettingsBuilder seaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
        return this;
    }

    public NoiseGeneratorSettingsBuilder disableMobGeneration() {
        disableMobGeneration = true;
        return this;
    }

    public NoiseGeneratorSettingsBuilder disableAquifers() {
        aquifers = false;
        return this;
    }

    public NoiseGeneratorSettingsBuilder disableOreVeins() {
        oreVeins = false;
        return this;
    }

    public NoiseGeneratorSettingsBuilder useLegacyRandomSource() {
        legacyRandomSource = true;
        return this;
    }

    @Override
    public NoiseGeneratorSettings createObject() {
        return new NoiseGeneratorSettings(
                noiseSettings,
                Validations.notNull(defaultBlock, "defaultBlock", sourceLine),
                Validations.notNull(defaultFluid, "defaultFluid", sourceLine),
                Validations.notNull(noiseRouter, "noiseRouter", sourceLine),
                surfaceRule,
                spawnTarget,
                seaLevel,
                disableMobGeneration,
                aquifers,
                oreVeins,
                legacyRandomSource
        );
    }
}
