package io.github.notenoughmail.worldjs.builders.cg;

import com.google.common.base.Suppliers;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.RegistryAccessContainer;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import io.github.notenoughmail.worldjs.WorldJS;
import io.github.notenoughmail.worldjs.builders.base.ChunkGeneratorBuilder;
import io.github.notenoughmail.worldjs.util.ServerRegistryHolderSet;
import io.github.notenoughmail.worldjs.util.Validations;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Info("A superflat world")
@ReturnsSelf
public class FlatChunkGeneratorBuilder extends ChunkGeneratorBuilder<FlatLevelSource> {

    private static final Supplier<Holder.Reference<Biome>> PLAINS = Suppliers.memoize(() ->
            Holder.Reference.createStandAlone(
                    RegistryAccessContainer.current.access().lookupOrThrow(Registries.BIOME),
                    ResourceKey.create(Registries.BIOME, WorldJS.mc("plains"))
            )
    );

    @Nullable
    public transient HolderSet<StructureSet> structureOverrides;
    public transient List<FlatLayerInfo> layers = new ArrayList<>();
    @Nullable
    public transient Holder.Reference<Biome> biome;
    public transient boolean lakes, decoration;

    public FlatChunkGeneratorBuilder(ResourceKey<LevelStem> id) {
        super(id);
    }

    @Info("The structure sets to place in the world. Defaults to all structure sets")
    public FlatChunkGeneratorBuilder structureOverrides(ServerRegistryHolderSet<StructureSet> structures) {
        structureOverrides = structures.convertWithValidation("structureOverrides", sourceLine);
        return this;
    }

    @Info("Adds a layer to the generator's settings")
    public FlatChunkGeneratorBuilder addLayer(int height, Block block) {
        layers.add(new FlatLayerInfo(
                Validations.assertRange(height, 0, DimensionType.Y_SIZE, "addLayer.height"),
                block
        ));
        return this;
    }

    @Info("The biome of the world. Defaults to 'minecraft:plains'")
    public FlatChunkGeneratorBuilder biome(Holder.Reference<Biome> biome) {
        this.biome = biome;
        return this;
    }

    @Info("Makes lava lakes spawn in the world")
    public FlatChunkGeneratorBuilder withLavaLakes() {
        lakes = true;
        return this;
    }

    @Info("Allows biome-specific placed features to generate")
    public FlatChunkGeneratorBuilder withBiomeFeatures() {
        decoration = true;
        return this;
    }

    @Override
    protected FlatLevelSource create() {
        final FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.ofNullable(structureOverrides),
                biome == null ? PLAINS.get() : biome,
                List.of()
        );
        settings.getLayersInfo().addAll(layers);
        if (lakes) settings.setAddLakes();
        if (decoration) settings.setDecoration();
        return new FlatLevelSource(settings);
    }
}
