package com.vulp.druidcraftrg.init;

import com.vulp.druidcraftrg.inventory.container.CrateContainer;
import net.minecraft.world.inventory.MenuType;

public class ContainerInit {

    public static MenuType<CrateContainer> CRATE_9x3 = new MenuType<>(CrateContainer::singleCrate);
    public static MenuType<CrateContainer> CRATE_9x6 = new MenuType<>(CrateContainer::doubleCrate);
    public static MenuType<CrateContainer> CRATE_9x12 = new MenuType<>(CrateContainer::quadCrate);
    public static MenuType<CrateContainer> CRATE_9x24 = new MenuType<>(CrateContainer::octoCrate);

}
