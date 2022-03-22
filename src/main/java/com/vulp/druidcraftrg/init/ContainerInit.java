package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import net.minecraft.inventory.container.ContainerType;

public class ContainerInit {

    public static ContainerType<CrateContainer> CRATE_9x3 = new ContainerType<>(CrateContainer::singleCrate);
    public static ContainerType<CrateContainer> CRATE_9x6 = new ContainerType<>(CrateContainer::doubleCrate);
    public static ContainerType<CrateContainer> CRATE_9x12 = new ContainerType<>(CrateContainer::quadCrate);
    public static ContainerType<CrateContainer> CRATE_9x24 = new ContainerType<>(CrateContainer::octoCrate);

}
